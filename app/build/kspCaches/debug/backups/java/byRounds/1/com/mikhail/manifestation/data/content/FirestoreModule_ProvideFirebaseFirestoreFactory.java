package com.mikhail.manifestation.data.content;

import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class FirestoreModule_ProvideFirebaseFirestoreFactory implements Factory<FirebaseFirestore> {
  @Override
  public FirebaseFirestore get() {
    return provideFirebaseFirestore();
  }

  public static FirestoreModule_ProvideFirebaseFirestoreFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseFirestore provideFirebaseFirestore() {
    return Preconditions.checkNotNullFromProvides(FirestoreModule.INSTANCE.provideFirebaseFirestore());
  }

  private static final class InstanceHolder {
    static final FirestoreModule_ProvideFirebaseFirestoreFactory INSTANCE = new FirestoreModule_ProvideFirebaseFirestoreFactory();
  }
}
