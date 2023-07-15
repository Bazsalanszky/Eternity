package eu.toldi.infinityforlemmy;

public interface UploadImageEnabledActivity {
    void uploadImage();
    void captureImage();
    void insertImageUrl(UploadedImage uploadedImage);
}
