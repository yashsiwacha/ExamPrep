# ════════════════════════════════════════════════════════════════════════════════
# ExamPrep OS — ProGuard / R8 Rules
# ════════════════════════════════════════════════════════════════════════════════

# ---------- Android Defaults ----------
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes Exceptions

# ---------- Kotlin ----------
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}

# ---------- Room (ORM) ----------
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-dontwarn androidx.room.**

# ---------- Hilt / Dagger ----------
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keep @javax.inject.** class * { *; }
-dontwarn dagger.hilt.**

# ---------- Coroutines ----------
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ---------- Compose ----------
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ---------- Timber ----------
-dontwarn timber.log.**

# ---------- Domain / Data Models ----------
-keep class com.examprep.domain.model.** { *; }
-keep class com.examprep.data.local.entity.** { *; }
-keep class com.examprep.data.local.seed.** { *; }

# ---------- Serialization (Type Converters) ----------
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ---------- General Android ----------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
