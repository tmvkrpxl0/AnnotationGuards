package com.tmvkrpxl0.annotationguards;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;

public class SubscribeEventGuard extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of("net.minecraftforge.eventbus.api.SubscribeEvent");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Types types = processingEnv.getTypeUtils();
        Elements elements = processingEnv.getElementUtils();
        TypeElement eventElement = elements.getTypeElement("net.minecraftforge.eventbus.api.Event");

        String EBSName = "net.minecraftforge.fml.common.Mod.EventBusSubscriber";
        var ebsMembers = elements.getAllMembers(elements.getTypeElement(EBSName)).stream().filter(member -> {
            boolean isExecutable = member instanceof ExecutableElement;
            boolean hasCorrectName = member.getSimpleName().contentEquals("value");
            hasCorrectName |= member.getSimpleName().contentEquals("bus");
            return isExecutable && hasCorrectName;
        }).toList();

        String clientEventPackage = "net.minecraftforge.client.event";
        Messager messager = processingEnv.getMessager();
        Set<? extends Element> root = roundEnv.getRootElements();

        final Set<? extends ExecutableElement> executables = (Set<? extends ExecutableElement>) roundEnv.getElementsAnnotatedWith(
                elements.getTypeElement("net.minecraftforge.eventbus.api.SubscribeEvent")
        );

        messager.printMessage(Diagnostic.Kind.NOTE, "Forge Event Annotation Processor Guard by tmvkrpxl0 is activated!");

        for (ExecutableElement executable : executables) {
            Set<Modifier> modifiers = executable.getModifiers();
            List<? extends VariableElement> arguments = executable.getParameters();

            if (modifiers.contains(Modifier.PRIVATE)) {
                messager.printMessage(Diagnostic.Kind.ERROR, String.format("%s: @SubscribeEvent needs non-private method!", root), executable);
            }

            if (arguments.size() != 1) {
                messager.printMessage(Diagnostic.Kind.ERROR, String.format("%s: @SubscribeEvent needs single argument method!", root), executable);
                continue;
            }

            VariableElement eventArgument = arguments.get(0);
            if (!types.isSubtype(eventArgument.asType(), eventElement.asType())) {
                messager.printMessage(Diagnostic.Kind.ERROR, String.format("%s: @SubscribeEvent needs subclass of Event as argument!", root), executable);
                continue;
            }

            executable.getEnclosingElement().getAnnotationMirrors().stream().filter(m -> {
                TypeElement mirror = ((TypeElement) m.getAnnotationType().asElement());
                return mirror.getQualifiedName().contentEquals(EBSName);
            }).findAny().ifPresent(ebs -> {
                if (!modifiers.contains(Modifier.STATIC)) {
                    messager.printMessage(Diagnostic.Kind.ERROR, String.format("%s: @SubscribeEvent in a class with @EventBusSubscriber needs static method!", root), executable);
                }
                var ebsValues = elements.getElementValuesWithDefaults(ebs);

                List ebsSides = (List) ebsValues.get((ExecutableElement) ebsMembers.get(0)).getValue();
                VariableElement ebsBus = (VariableElement) ebsValues.get((ExecutableElement) ebsMembers.get(1)).getValue();

                boolean isEventClassClient = eventArgument.asType().toString().contains(clientEventPackage); // Should it also check for dedicated-server only events?

                if (isEventClassClient && ebsSides.stream().noneMatch(side -> side.toString().contentEquals("CLIENT"))) {
                    messager.printMessage(Diagnostic.Kind.WARNING,
                            String.format("%s: This event handler will not listen to specified event, as the event is client-only event and @EventBusSubscriber of enclosing class is configured to ignore clients", root),
                            executable);
                }

                boolean isEventModBus = types.isSubtype(eventArgument.asType(), elements.getTypeElement("net.minecraftforge.fml.event.IModBusEvent").asType());

                if ("FORGE".contentEquals(ebsBus.toString()) && isEventModBus) {
                    messager.printMessage(Diagnostic.Kind.WARNING,
                            String.format("%s: This event handler will not listen to specified event, as the event is MOD bus event but @EventBusSubscriber of enclosing class is configured to only listen for FORGE bus.", root),
                            executable);
                } else if ("MOD".contentEquals(ebsBus.toString()) && !isEventModBus) {
                    messager.printMessage(Diagnostic.Kind.WARNING,
                            String.format("%s: This event handler will not listen to specified event, as the event is FORGE bus event but @EventBusSubscriber of enclosing class is configured to only listen for MOD bus.", root),
                            executable);
                }
            });
        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
