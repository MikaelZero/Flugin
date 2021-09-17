package github.mikaelzero.flugin.apt

import com.google.auto.service.AutoService
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import com.sun.tools.javac.code.Symbol
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

@AutoService(Processor::class)
@SupportedAnnotationTypes(
    "net.mikaelzero.flugin.interfaces.FlutterPlugin",
    "net.mikaelzero.flugin.interfaces.FlutterMethodCall"
)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class PluginProcessor : AbstractProcessor() {
    private var elementUtils: Elements? = null
    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        elementUtils = processingEnv?.elementUtils
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        if (annotations == null || annotations.isEmpty()) {
            return false
        }
        val builder = CodeBlock.builder()
        for (element in roundEnv.getElementsAnnotatedWith(FlutterPlugin::class.java)) {
            if (element !is Symbol.ClassSymbol) {
                continue
            }
            val cls: Symbol.ClassSymbol = element
            val flutterPlugin: FlutterPlugin =
                cls.getAnnotation(FlutterPlugin::class.java) ?: continue
            val b = CodeBlock.builder()
            b.add("new \$T()", cls)
            val codeBlock = b.build()
            builder.addStatement(
                "FluginBridgeProxy.getInstance().putClass(\$S, \$L)",
                flutterPlugin.name,
                codeBlock
            )
            for (elementMethod in roundEnv.getElementsAnnotatedWith(FlutterMethodCall::class.java)) {

                if (elementMethod !is Symbol.MethodSymbol) {
                    continue
                }
                val packageName: String =
                    elementUtils?.getPackageOf(elementMethod)?.qualifiedName.toString()
                if (element.fullname.toString() == packageName + "." + elementMethod.enclosingElement.simpleName.toString()
                ) {
                    val meS: Symbol.MethodSymbol = elementMethod
                    val flutterMethodCall: FlutterMethodCall =
                        meS.getAnnotation(FlutterMethodCall::class.java) ?: continue
                    builder.addStatement(
                        "FluginBridgeProxy.getInstance().putMethod(\$S,\$S, \$S)",
                        flutterPlugin.name, flutterMethodCall.name, elementMethod.name
                    )
                }
            }
        }

        val main = MethodSpec.methodBuilder("init")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(Void.TYPE)
            .addCode(builder.build())
            .build()
        val compileClass = TypeSpec.classBuilder("FluginCompileClass")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addMethod(main)
            .build()
        val javaFile = JavaFile.builder("net.mikaelzero.flugin", compileClass)
            .build()
        try {
            javaFile.writeTo(processingEnv.filer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return true
    }
}