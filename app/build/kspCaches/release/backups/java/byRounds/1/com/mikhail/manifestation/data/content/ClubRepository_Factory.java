package com.mikhail.manifestation.data.content;

import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
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
public final class ClubRepository_Factory implements Factory<ClubRepository> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  public ClubRepository_Factory(Provider<FirebaseFirestore> firestoreProvider) {
    this.firestoreProvider = firestoreProvider;
  }

  @Override
  public ClubRepository get() {
    return newInstance(firestoreProvider.get());
  }

  public static ClubRepository_Factory create(Provider<FirebaseFirestore> firestoreProvider) {
    return new ClubRepository_Factory(firestoreProvider);
  }

  public static ClubRepository newInstance(FirebaseFirestore firestore) {
    return new ClubRepository(firestore);
  }
}
