# ==========================
# 🚀 Keep Essential Model Classes
# ==========================
-keep class com.popiin.res.** { *; }
-keep class com.popiin.req.** { *; }
-keep class com.popiin.model.** { *; }

# Keep all fields with @SerializedName annotation
-keepclassmembers class com.popiin.res.** {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keepclassmembers class com.popiin.req.** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ==========================
# 🌍 Retrofit & OkHttp (API Calls)
# ==========================
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keep class com.popiin.ApiInterface.** { *; }

# ==========================
# 📦 Gson (Prevent Model Obfuscation)
# ==========================
-keep class com.google.gson.** { *; }
-keepattributes *Annotation*

# ==========================
# 🚀 Keep Kotlin Metadata (Prevent Class Removal)
# ==========================
-keep class kotlin.Metadata { *; }
-keepclassmembers class ** {
    @kotlin.Metadata *;
}

# ==========================
# 📦 Parcelable (Kotlin @Parcelize)
# ==========================
-keep class * implements android.os.Parcelable { *; }

# ==========================
# 🎨 ViewBinding (Prevent Removal of ViewBinding Classes)
# ==========================
-keep class * extends androidx.viewbinding.ViewBinding { *; }

# ==========================
# 🔄 Jetpack Navigation Components
# ==========================
-keep class androidx.navigation.** { *; }

# ==========================
# 🔥 Firebase SDK (Prevent Issues)
# ==========================
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# ==========================
# 🖼️ Glide (If Used)
# ==========================
-keep class com.bumptech.glide.** { *; }
-keep class * extends com.bumptech.glide.module.AppGlideModule { *; }
-keep class * extends com.bumptech.glide.module.LibraryGlideModule { *; }

# ==========================
# 🛠️ WorkManager (If Used)
# ==========================
-keep class androidx.work.** { *; }

# ==========================
# 🛑 Prevent Issues with Reflection
# ==========================
-dontwarn java.lang.invoke.**

# ==========================
# 📌 Remove Debug Log Messages in Release Builds
# ==========================
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
