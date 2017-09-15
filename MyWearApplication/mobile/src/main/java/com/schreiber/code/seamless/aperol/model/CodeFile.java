package com.schreiber.code.seamless.aperol.model;


import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.firebase.database.DataSnapshot;
import com.schreiber.code.seamless.aperol.util.BitmapUtils;
import com.schreiber.code.seamless.aperol.util.TypeUtils;

import java.util.ArrayList;
import java.util.List;

import me.mattlogan.auto.value.firebase.annotation.FirebaseValue;


@AutoValue
@FirebaseValue
public abstract class CodeFile implements Parcelable {

    static final long NOT_ON_WATCH = 0L;

    public static CodeFile create(OriginalCodeFile originalCodeFile, Bitmap originalImage) {
        return create(originalCodeFile, originalImage, null);
    }

    public static CodeFile create(OriginalCodeFile originalCodeFile, Bitmap originalImage, @Nullable Bitmap codeImage) {
        String codeType = "";
        String codeContentType = "";
        String codeDisplayContent = "";
        String codeRawContent = "";
        return create(originalCodeFile, originalImage, codeImage, codeType, codeContentType, codeDisplayContent, codeRawContent);
    }

    public static CodeFile create(OriginalCodeFile originalCodeFile, Bitmap originalImage, Bitmap codeImage, String codeType, String codeContentType, String codeDisplayContent, String codeRawContent) {
        String originalFilename = originalCodeFile.filename();
        String suffix = CodeFileFactory.getFileSuffix(originalFilename);
        String displayName = originalFilename.replace("." + suffix, "");
        List<String> tags = createTags(originalCodeFile, suffix, codeType, codeContentType);
        String originalImageBase64encoded = getBase64encodedBitmap(originalImage);
        String codeImageBase64encoded = getBase64encodedBitmap(codeImage);

        return new AutoValue_CodeFile(displayName, tags, codeType, codeContentType, codeDisplayContent, codeRawContent, originalCodeFile, NOT_ON_WATCH, originalImageBase64encoded, codeImageBase64encoded);
    }

    public static CodeFile create(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(AutoValue_CodeFile.FirebaseValue.class).toAutoValue();
    }

    public Object toFirebaseValue() {
        return new AutoValue_CodeFile.FirebaseValue(this);
    }

    private static List<String> createTags(OriginalCodeFile originalCodeFile, String suffix, String codeType, String codeContentType) {
        List<String> tags = new ArrayList<>();
        tags.add(originalCodeFile.filename());
        tags.add(suffix);
        tags.add(originalCodeFile.fileType());
        tags.add(originalCodeFile.importedFrom());
        if (!TypeUtils.isEmpty(codeType)) {
            tags.add(codeType);
        }
        if (!TypeUtils.isEmpty(codeContentType)) {
            tags.add(codeContentType);
        }
        return tags;
    }

    public abstract String displayName();

    public abstract List<String> tags();

    public abstract String codeType();

    public abstract String codeContentType();

    public abstract String codeDisplayContent();

    public abstract String codeRawContent();

    public abstract OriginalCodeFile originalCodeFile();

    public abstract long onWatchUntil();

    public String id() {
        // TODO [Before beta] Decide on what to use as an id
        return String.valueOf(hashCode());
    }

    public boolean isCodeAvailable() {
        return codeImageBytes() != null;
    }

    @Nullable
    abstract String originalImageBytes();

    @Nullable
    abstract String codeImageBytes();

    Bitmap originalImage() {
        return createBitmapFromBase64EncodedString(originalImageBytes());
    }

    @Nullable
    Bitmap codeImage() {
        return createBitmapFromBase64EncodedString(codeImageBytes());
    }

    Bitmap originalImageThumbnail() {
        return createThumbnail(originalImage());
    }

    @Nullable
    Bitmap codeImageThumbnail() {
        return createThumbnail(codeImage());
    }

    private static Bitmap createThumbnail(Bitmap originalImage) {
        return BitmapUtils.resizeImage(originalImage, 200);
    }

    @Nullable
    private Bitmap createBitmapFromBase64EncodedString(@Nullable String bitmapBase64Encoded) {
        return BitmapUtils.createBitmapFromBase64EncodedString(bitmapBase64Encoded);
    }

    private static String getBase64encodedBitmap(Bitmap bitmap) {
        return BitmapUtils.getBase64encodedBitmap(bitmap);
    }

}

