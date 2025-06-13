package de.uni_passau.fim.talent_tauscher.model.business.services;

import de.uni_passau.fim.talent_tauscher.model.persistence.util.ResourceReader;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.function.Function;

/** Manages interactions with {@link ResourceReader}. */
@ApplicationScoped
public class ResourceService implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private static final String BASE_PATH = "WEB-INF/classes/";

  /**
   * Gets the default advertisement image.
   *
   * @param contextStreamProvider A function to fetch resources as InputStreams based on a given
   *     path.
   * @return The content of the image file as a byte array.
   * @author Jakob Edmaier
   */
  public byte[] getDefaultAdvertisementImage(Function<String, InputStream> contextStreamProvider) {
    return new ResourceReader(contextStreamProvider, BASE_PATH).fetchDefaultAdvertisementImage();
  }

  /**
   * Gets the default Avatar image.
   *
   * @param contextStreamProvider A function to fetch resources as InputStreams based on a given
   *     path.
   * @return The content of the image file as a byte array.
   * @author Sturm
   */
  public byte[] getDefaultAvatarImage(Function<String, InputStream> contextStreamProvider) {
    return new ResourceReader(contextStreamProvider, BASE_PATH).fetchDefaultAvatarImage();
  }

  /**
   * Gets the default Avatar image.
   *
   * @param contextStreamProvider A function to fetch resources as InputStreams based on a given
   *     path.
   * @return The content of the image file as a byte array.
   * @author Sturm
   */
  public byte[] getDefaultLogoImage(Function<String, InputStream> contextStreamProvider) {
    return new ResourceReader(contextStreamProvider, BASE_PATH).fetchDefaultLogoImage();
  }
}
