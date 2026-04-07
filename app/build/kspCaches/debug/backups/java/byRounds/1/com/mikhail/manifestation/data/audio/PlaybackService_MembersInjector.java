package com.mikhail.manifestation.data.audio;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class PlaybackService_MembersInjector implements MembersInjector<PlaybackService> {
  private final Provider<AudioPlayerManager> audioPlayerManagerProvider;

  public PlaybackService_MembersInjector(Provider<AudioPlayerManager> audioPlayerManagerProvider) {
    this.audioPlayerManagerProvider = audioPlayerManagerProvider;
  }

  public static MembersInjector<PlaybackService> create(
      Provider<AudioPlayerManager> audioPlayerManagerProvider) {
    return new PlaybackService_MembersInjector(audioPlayerManagerProvider);
  }

  @Override
  public void injectMembers(PlaybackService instance) {
    injectAudioPlayerManager(instance, audioPlayerManagerProvider.get());
  }

  @InjectedFieldSignature("com.mikhail.manifestation.data.audio.PlaybackService.audioPlayerManager")
  public static void injectAudioPlayerManager(PlaybackService instance,
      AudioPlayerManager audioPlayerManager) {
    instance.audioPlayerManager = audioPlayerManager;
  }
}
