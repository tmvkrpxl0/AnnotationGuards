package com.tmvkrpxl0.annotationguards;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;

public class EventBusSubscriberGuard extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of("net.minecraftforge.fml.common.Mod.EventBusSubscriber");
    }
    @Override
    @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent", "SuspiciousMethodCalls"})
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Elements elements = processingEnv.getElementUtils();

        Messager messager = processingEnv.getMessager();
        Set<? extends Element> root = roundEnv.getRootElements();

        String EBSName = "net.minecraftforge.fml.common.Mod.EventBusSubscriber";
        var modIdElement = elements.getAllMembers(elements.getTypeElement(EBSName)).stream()
                .filter(member -> member.getSimpleName().contentEquals("modid"))
                .findAny().get();

        final Set<? extends TypeElement> typeElements = (Set<? extends TypeElement>) roundEnv.getElementsAnnotatedWith(elements.getTypeElement(EBSName));
        for (TypeElement typeElement : typeElements) {
            AnnotationMirror ebsMirror = typeElement.getAnnotationMirrors().stream()
                    .filter(mirror -> ((TypeElement)mirror.getAnnotationType().asElement()).getQualifiedName().contentEquals(EBSName))
                    .findAny().get();
            var values = ebsMirror.getElementValues();

            if (values.containsKey(modIdElement)) continue;

            if (typeElement.getEnclosingElement() instanceof TypeElement outerClass) {
                List<? extends AnnotationMirror> modAnnotations = outerClass.getAnnotationMirrors().stream()
                        .filter(mirror -> "net.minecraftforge.fml.common.Mod".contentEquals(mirror.getAnnotationType().toString())).toList();
                if (modAnnotations.size() != 0) {
                    continue;
                }
            }

            messager.printMessage(Diagnostic.Kind.ERROR, String.format("%s: @EventBusSubscriber annotations must have mod id specified when it's not nested by another class with @Mod annotation.", root), typeElement, ebsMirror);
        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
