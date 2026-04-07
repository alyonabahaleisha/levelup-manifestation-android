package com.mikhail.manifestation.ui.viewmodel;

import com.mikhail.manifestation.data.content.ClubRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class ClubsViewModel_Factory implements Factory<ClubsViewModel> {
  private final Provider<ClubRepository> clubRepositoryProvider;

  public ClubsViewModel_Factory(Provider<ClubRepository> clubRepositoryProvider) {
    this.clubRepositoryProvider = clubRepositoryProvider;
  }

  @Override
  public ClubsViewModel get() {
    return newInstance(clubRepositoryProvider.get());
  }

  public static ClubsViewModel_Factory create(Provider<ClubRepository> clubRepositoryProvider) {
    return new ClubsViewModel_Factory(clubRepositoryProvider);
  }

  public static ClubsViewModel newInstance(ClubRepository clubRepository) {
    return new ClubsViewModel(clubRepository);
  }
}
