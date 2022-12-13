package com.tmvkrpxl0;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Set;

public class SubscribeEventGuard extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(SubscribeEvent.class.getCanonicalName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Types types = processingEnv.getTypeUtils();
        Elements elements = processingEnv.getElementUtils();
        String EBSName = "net.minecraftforge.fml.common.Mod.EventBusSubscriber";

        final Set<? extends ExecutableElement> executables = (Set<? extends ExecutableElement>) roundEnv.getElementsAnnotatedWith(SubscribeEvent.class);
        for (ExecutableElement executable : executables) {
            Set<Modifier> modifiers = executable.getModifiers();
            List<? extends VariableElement> arguments = executable.getParameters();

            if (modifiers.contains(Modifier.PRIVATE)) {
                throw new IllegalStateException("@SubscribeEvent needs non-private method!");
            }

            if (arguments.size() != 1) {
                throw new IllegalArgumentException("@SubscribeEvent needs single argument method!");
            }

            VariableElement first = arguments.get(0);
            TypeElement eventElement = elements.getTypeElement(Event.class.getName());
            if (!types.isSubtype(first.asType(), eventElement.asType())) {
                throw new ClassCastException("@SubscribeEvent needs subclass of Event as argument!");
            }

            executable.getEnclosingElement().getAnnotationMirrors().stream().filter(m -> {
                TypeElement mirror = ((TypeElement) m.getAnnotationType().asElement());
                return mirror.getQualifiedName().contentEquals(EBSName);
            }).findAny().ifPresent(ebs -> {
                if (!modifiers.contains(Modifier.STATIC)) {
                    throw new IllegalStateException("@SubscribeEvent in a class with @EventBusSubscriber must be static!");
                }
                var values = elements.getElementValuesWithDefaults(ebs);
                var members = elements.getAllMembers(elements.getTypeElement(EBSName)).stream().filter(member -> {
                    boolean isExecutable = member instanceof ExecutableElement;
                    System.out.println(member.getSimpleName());
                    boolean hasCorrectName = member.getSimpleName().contentEquals("value");
                    hasCorrectName |= member.getSimpleName().contentEquals("bus");
                    return isExecutable && hasCorrectName;
                }).toList();

                Element sides = members.get(0);
                Element bus = members.get(1);
            });
        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
