-keep class uk.akane.aether.PluginMain { *; }

-keep class kotlin.collections.** { *; }
-keep class kotlin.coroutines.** { *; }
-keep class kotlin.data.collections.** { *; }
-keep class kotlin.jvm.** { *; }
-keep class kotlin.Lazy { *; }
-keep class kotlin.Pair { *; }
-keep class kotlin.properties.** { *; }
-keep class kotlin.ranges.** { *; }
-keep class kotlin.reflect.** { *; }
-keep class kotlin.sequences.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class kotlinx.sequences.** { *; }
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlin.collections.**
-dontwarn kotlin.coroutines.**
-dontwarn kotlin.data.collections.**
-dontwarn kotlin.jvm.**
-dontwarn kotlin.Lazy
-dontwarn kotlin.Pair
-dontwarn kotlin.properties.**
-dontwarn kotlin.ranges.**
-dontwarn kotlin.reflect.**
-dontwarn kotlin.sequences.**
-dontwarn kotlinx.coroutines.**
-dontwarn kotlinx.sequences.**
-dontwarn kotlinx.serialization.**

-keep class java.lang.invoke.** { *; }
-keep class java.beans.** { *; }
-dontwarn java.lang.invoke.**
-dontwarn java.beans.**

-keep class io.netty.internal.tcnative.** { *; }
-dontwarn io.netty.internal.tcnative.**

-keep class sun.misc.Unsafe { *; }
-dontwarn sun.misc.Unsafe

-dontwarn com.oracle.svm.core.annotate.**
-dontwarn io.netty.util.internal.logging.**
-dontwarn java.util.logging.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.osgi.annotation.**

-dontwarn com.aayushatharva.brotli4j.**
-dontwarn com.jcraft.jzlib.**
-dontwarn net.jpountz.lz4.**
-dontwarn net.jpountz.xxhash.**
-dontwarn com.ning.compress.**
-dontwarn lzma.sdk.lzma.**
-dontwarn com.github.luben.zstd.**

-keep class reactor.blockhound.** { *; }
-keep class org.bouncycastle.** { *; }
-keep class org.conscrypt.** { *; }
-keep class io.netty.pkitesting.** { *; }
-keep class com.sun.nio.** { *; }
-dontwarn reactor.blockhound.**
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn io.netty.pkitesting.**
-dontwarn com.sun.nio.**

-dontwarn java.lang.management.**

-dontwarn org.apache.logging.**
-dontwarn io.netty.util.internal.logging.**
-dontwarn org.apache.log4j.**
-dontwarn org.slf4j.**
-keep class io.netty.** { *; }
-keep class io.ktor.** { *; }
-keep class org.apache.logging.** { *; }
-keep class org.apache.log4j.** { *; }
-keep class org.slf4j.** { *; }

-dontnote