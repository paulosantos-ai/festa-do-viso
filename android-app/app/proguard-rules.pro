# Add project specific ProGuard rules here.

# Keep Room database classes
-keep class com.festadoviso.data.local.entity.** { *; }
-keep class com.festadoviso.data.local.dao.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep BCrypt
-keep class org.mindrot.jbcrypt.** { *; }

# Kotlinx Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
