package eu.toldi.infinityforlemmy.post;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import eu.toldi.infinityforlemmy.community.BasicCommunityInfo;
import eu.toldi.infinityforlemmy.user.BasicUserInfo;

/**
 * Created by alex on 3/1/18.
 */

public class Post implements Parcelable {
    public static final int NSFW_TYPE = -1;
    public static final int TEXT_TYPE = 0;
    public static final int IMAGE_TYPE = 1;
    public static final int LINK_TYPE = 2;
    public static final int VIDEO_TYPE = 3;
    public static final int GIF_TYPE = 4;
    public static final int NO_PREVIEW_LINK_TYPE = 5;
    public static final int GALLERY_TYPE = 6;
    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
    private int id;
    private BasicCommunityInfo communityInfo;
    private BasicUserInfo author;
    private String title;
    private String selfText;
    private String selfTextPlain;
    private String selfTextPlainTrimmed;
    private String url;
    private String videoUrl;
    private String videoDownloadUrl;
    private String redgifsId;
    private String streamableShortCode;
    private boolean isImgur;
    private boolean isRedgifs;
    private boolean isStreamable;
    private boolean loadVideoSuccess;
    private String permalink;
    private long postTimeMillis;

    private int downvotes;

    private int upvotes;
    private int postType;
    private int voteType;
    private int nComments;
    private int upvoteRatio;
    private boolean hidden;
    private boolean nsfw;
    private boolean featuredInCommunity;

    private boolean featuredOnInstance;
    private boolean archived;
    private boolean locked;
    private boolean saved;
    private boolean isCrosspost;
    private boolean isRead;

    private boolean deleted;
    private String crosspostParentId;
    private String distinguished;
    private String suggestedSort;
    private ArrayList<Preview> previews = new ArrayList<>();
    private ArrayList<Gallery> gallery = new ArrayList<>();

    public Post(int id, BasicCommunityInfo communityInfo,
                BasicUserInfo userInfo, long postTimeMillis,
                String title, String permalink, int downvotes, int upvotes, int postType, int voteType, int nComments,
                int upvoteRatio,
                boolean nsfw, boolean locked, boolean saved, boolean deleted,
                String distinguished, String suggestedSort) {
        this.id = id;
        this.communityInfo = communityInfo;
        this.author = userInfo;
        this.postTimeMillis = postTimeMillis;
        this.title = title;
        this.permalink = permalink;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.postType = postType;
        this.voteType = voteType;
        this.nComments = nComments;
        this.upvoteRatio = upvoteRatio;
        this.hidden = hidden;
        this.nsfw = nsfw;
        this.archived = archived;
        this.deleted = deleted;
        this.locked = locked;
        this.saved = saved;
        this.isCrosspost = isCrosspost;
        this.distinguished = distinguished;
        this.suggestedSort = suggestedSort;
        isRead = false;
    }

    public Post(int id, BasicCommunityInfo communityInfo,
                BasicUserInfo author, long postTimeMillis, String title,
                String url, String permalink, int downvotes, int upvotes, int postType, int voteType, int nComments,
                int upvoteRatio,
                boolean nsfw, boolean locked, boolean saved, boolean deleted, String distinguished, String suggestedSort) {
        this.id = id;
        this.communityInfo = communityInfo;
        this.author = author;
        this.postTimeMillis = postTimeMillis;
        this.title = title;
        this.url = url;
        this.permalink = permalink;
        this.downvotes = downvotes;
        this.upvotes = upvotes;
        this.postType = postType;
        this.voteType = voteType;
        this.nComments = nComments;
        this.upvoteRatio = upvoteRatio;
        this.hidden = hidden;
        this.nsfw = nsfw;
        this.archived = archived;
        this.locked = locked;
        this.saved = saved;
        this.deleted = deleted;
        this.isCrosspost = isCrosspost;
        this.distinguished = distinguished;
        this.suggestedSort = suggestedSort;
        isRead = false;
    }

    protected Post(Parcel in) {
        id = in.readInt();
        communityInfo = in.readParcelable(BasicCommunityInfo.class.getClassLoader());
        author = in.readParcelable(BasicUserInfo.class.getClassLoader());
        postTimeMillis = in.readLong();
        title = in.readString();
        selfText = in.readString();
        selfTextPlain = in.readString();
        selfTextPlainTrimmed = in.readString();
        url = in.readString();
        videoUrl = in.readString();
        videoDownloadUrl = in.readString();
        redgifsId = in.readString();
        streamableShortCode = in.readString();
        isImgur = in.readByte() != 0;
        isRedgifs = in.readByte() != 0;
        isStreamable = in.readByte() != 0;
        loadVideoSuccess = in.readByte() != 0;
        permalink = in.readString();
        downvotes = in.readInt();
        upvotes = in.readInt();
        postType = in.readInt();
        voteType = in.readInt();
        nComments = in.readInt();
        upvoteRatio = in.readInt();
        hidden = in.readByte() != 0;
        nsfw = in.readByte() != 0;
        featuredInCommunity = in.readByte() != 0;
        archived = in.readByte() != 0;
        locked = in.readByte() != 0;
        saved = in.readByte() != 0;
        deleted = in.readByte() != 0;
        isCrosspost = in.readByte() != 0;
        isRead = in.readByte() != 0;
        crosspostParentId = in.readString();
        distinguished = in.readString();
        suggestedSort = in.readString();
        in.readTypedList(previews, Preview.CREATOR);
        in.readTypedList(gallery, Gallery.CREATOR);
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return communityInfo.getQualifiedName();
    }

    public String getSubredditName() {
        return communityInfo.getDisplayName();
    }

    public String getSubredditNamePrefixed() {
        return communityInfo.getQualifiedName();
    }

    public String getSubredditIconUrl() {
        return communityInfo.getIcon();
    }


    public String getAuthor() {
        return author.getDisplayName();
    }

    public boolean isAuthorDeleted() {
        return author != null && author.equals("[deleted]");
    }

    public void setAuthor(String author) {

    }

    public String getAuthorNamePrefixed() {
        return author.getQualifiedName();
    }

    public String getAuthorIconUrl() {
        return (author.getAvatar() == null) ? "" : author.getAvatar();
    }

    public void setAuthorIconUrl(String authorIconUrl) {

    }

    public long getPostTimeMillis() {
        return postTimeMillis;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSelfText() {
        return selfText;
    }

    public void setSelfText(String selfText) {
        this.selfText = selfText;
    }

    public String getSelfTextPlain() {
        return selfTextPlain;
    }

    public void setSelfTextPlain(String selfTextPlain) {
        this.selfTextPlain = selfTextPlain;
    }

    public String getSelfTextPlainTrimmed() {
        return selfTextPlainTrimmed;
    }

    public void setSelfTextPlainTrimmed(String selfTextPlainTrimmed) {
        this.selfTextPlainTrimmed = selfTextPlainTrimmed;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoDownloadUrl() {
        return videoDownloadUrl;
    }

    public void setVideoDownloadUrl(String videoDownloadUrl) {
        this.videoDownloadUrl = videoDownloadUrl;
    }

    public String getRedgifsId() {
        return redgifsId;
    }

    public void setRedgifsId(String redgifsId) {
        this.redgifsId = redgifsId;
    }

    public String getStreamableShortCode() {
        return streamableShortCode;
    }

    public void setStreamableShortCode(String shortCode) {
        this.streamableShortCode = shortCode;
    }

    public void setIsImgur(boolean isImgur) {
        this.isImgur = isImgur;
    }

    public boolean isImgur() {
        return isImgur;
    }

    public boolean isRedgifs() {
        return isRedgifs;
    }

    public void setIsRedgifs(boolean isRedgifs) {
        this.isRedgifs = isRedgifs;
    }

    public boolean isStreamable() {
        return isStreamable;
    }

    public void setIsStreamable(boolean isStreamable) {
        this.isStreamable = isStreamable;
    }

    public boolean isLoadVideoSuccess() {
        return loadVideoSuccess;
    }

    public void setLoadVideoSuccess(boolean loadVideoSuccess) {
        this.loadVideoSuccess = loadVideoSuccess;
    }

    public String getPermalink() {
        return permalink;
    }

    public boolean isModerator() {
        return distinguished != null && distinguished.equals("moderator");
    }

    public boolean isAdmin() {
        return distinguished != null && distinguished.equals("admin");
    }

    public String getSuggestedSort() {
        return suggestedSort;
    }

    public int getScore() {
        return upvotes- downvotes;
    }

    public int getPostType() {
        return postType;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public int getVoteType() {
        return voteType;
    }

    public void setVoteType(int voteType) {
        this.voteType = voteType;
    }

    public int getNComments() {
        return nComments;
    }

    public void setNComments(int nComments) {
        this.nComments = nComments;
    }

    public int getUpvoteRatio() {
        return upvoteRatio;
    }

    public void setUpvoteRatio(int upvoteRatio) {
        this.upvoteRatio = upvoteRatio;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isNSFW() {
        return nsfw;
    }

    public void setNSFW(boolean nsfw) {
        this.nsfw = nsfw;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isFeaturedInCommunity() {
        return featuredInCommunity;
    }

    public void setFeaturedInCommunity(boolean featuredInCommunity) {
        this.featuredInCommunity = featuredInCommunity;
    }

    public boolean isFeaturedOnInstance() {
        return featuredOnInstance;
    }

    public void setFeaturedOnInstance(boolean featuredOnInstance) {
        this.featuredOnInstance = featuredOnInstance;
    }

    public boolean isArchived() {
        return archived;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isCrosspost() {
        return isCrosspost;
    }

    public void markAsRead() {
        isRead = true;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getCrosspostParentId() {
        return crosspostParentId;
    }

    public void setCrosspostParentId(String crosspostParentId) {
        this.crosspostParentId = crosspostParentId;
    }

    public ArrayList<Preview> getPreviews() {
        return previews;
    }

    public void setPreviews(ArrayList<Preview> previews) {
        this.previews = previews;
    }

    public ArrayList<Gallery> getGallery() {
        return gallery;
    }

    public void setGallery(ArrayList<Gallery> gallery) {
        this.gallery = gallery;
    }

    public BasicCommunityInfo getCommunityInfo() {
        return communityInfo;
    }

    public BasicUserInfo getAuthorInfo() {
        return author;
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeParcelable(communityInfo, i);
        parcel.writeParcelable(author, i);
        parcel.writeLong(postTimeMillis);
        parcel.writeString(title);
        parcel.writeString(selfText);
        parcel.writeString(selfTextPlain);
        parcel.writeString(selfTextPlainTrimmed);
        parcel.writeString(url);
        parcel.writeString(videoUrl);
        parcel.writeString(videoDownloadUrl);
        parcel.writeString(redgifsId);
        parcel.writeString(streamableShortCode);
        parcel.writeByte((byte) (isImgur ? 1 : 0));
        parcel.writeByte((byte) (isRedgifs ? 1 : 0));
        parcel.writeByte((byte) (isStreamable ? 1 : 0));
        parcel.writeByte((byte) (loadVideoSuccess ? 1 : 0));
        parcel.writeString(permalink);
        parcel.writeInt(downvotes);
        parcel.writeInt(upvotes);
        parcel.writeInt(postType);
        parcel.writeInt(voteType);
        parcel.writeInt(nComments);
        parcel.writeInt(upvoteRatio);
        parcel.writeByte((byte) (hidden ? 1 : 0));
        parcel.writeByte((byte) (nsfw ? 1 : 0));
        parcel.writeByte((byte) (featuredInCommunity ? 1 : 0));
        parcel.writeByte((byte) (archived ? 1 : 0));
        parcel.writeByte((byte) (locked ? 1 : 0));
        parcel.writeByte((byte) (saved ? 1 : 0));
        parcel.writeByte((byte) (deleted ? 1 : 0));
        parcel.writeByte((byte) (isCrosspost ? 1 : 0));
        parcel.writeByte((byte) (isRead ? 1 : 0));
        parcel.writeString(crosspostParentId);
        parcel.writeString(distinguished);
        parcel.writeString(suggestedSort);
        parcel.writeTypedList(previews);
        parcel.writeTypedList(gallery);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Post)) {
            return false;
        }
        return ((Post) obj).id == id;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(id).hashCode();
    }

    public static class Gallery implements Parcelable {
        public static final int TYPE_IMAGE = 0;
        public static final int TYPE_GIF = 1;
        public static final int TYPE_VIDEO = 2;

        public String mimeType;
        public String url;
        public String fallbackUrl;
        private boolean hasFallback;
        public String fileName;
        public int mediaType;
        public String caption;
        public String captionUrl;

        public Gallery(String mimeType, String url, String fallbackUrl, String fileName, String caption, String captionUrl) {
            this.mimeType = mimeType;
            this.url = url;
            this.fallbackUrl = fallbackUrl;
            this.fileName = fileName;
            if (mimeType.contains("gif")) {
                mediaType = TYPE_GIF;
            } else if (mimeType.contains("jpg") || mimeType.contains("png")) {
                mediaType = TYPE_IMAGE;
            } else {
                mediaType = TYPE_VIDEO;
            }
            this.caption = caption;
            this.captionUrl = captionUrl;
        }

        protected Gallery(Parcel in) {
            mimeType = in.readString();
            url = in.readString();
            fallbackUrl = in.readString();
            hasFallback = in.readByte() != 0;
            fileName = in.readString();
            mediaType = in.readInt();
            caption = in.readString();
            captionUrl = in.readString();
        }

        public static final Creator<Gallery> CREATOR = new Creator<Gallery>() {
            @Override
            public Gallery createFromParcel(Parcel in) {
                return new Gallery(in);
            }

            @Override
            public Gallery[] newArray(int size) {
                return new Gallery[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(mimeType);
            parcel.writeString(url);
            parcel.writeString(fallbackUrl);
            parcel.writeByte((byte) (hasFallback ? 1 : 0));
            parcel.writeString(fileName);
            parcel.writeInt(mediaType);
            parcel.writeString(caption);
            parcel.writeString(captionUrl);
        }

        public void setFallbackUrl(String fallbackUrl) { this.fallbackUrl = fallbackUrl; }

        public void setHasFallback(boolean hasFallback) { this.hasFallback = hasFallback; }

        public boolean hasFallback() { return this.hasFallback; }
    }

    public static class Preview implements Parcelable {
        private String previewUrl;
        private int previewWidth;
        private int previewHeight;
        private String previewCaption;
        private String previewCaptionUrl;

        private Bitmap previewBitmap;

        public Preview(String previewUrl, int previewWidth, int previewHeight, String previewCaption, String previewCaptionUrl) {
            this.previewUrl = previewUrl;
            this.previewWidth = previewWidth;
            this.previewHeight = previewHeight;
            this.previewCaption = previewCaption;
            this.previewCaptionUrl = previewCaptionUrl;
        }

        protected Preview(Parcel in) {
            previewUrl = in.readString();
            previewWidth = in.readInt();
            previewHeight = in.readInt();
            previewCaption = in.readString();
            previewCaptionUrl = in.readString();
            previewBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        }

        public static final Creator<Preview> CREATOR = new Creator<Preview>() {
            @Override
            public Preview createFromParcel(Parcel in) {
                return new Preview(in);
            }

            @Override
            public Preview[] newArray(int size) {
                return new Preview[size];
            }
        };

        public String getPreviewUrl() {
            return previewUrl;
        }

        public void setPreviewUrl(String previewUrl) {
            this.previewUrl = previewUrl;
        }

        public int getPreviewWidth() {
            return previewWidth;
        }

        public void setPreviewWidth(int previewWidth) {
            this.previewWidth = previewWidth;
        }

        public int getPreviewHeight() {
            return previewHeight;
        }

        public void setPreviewHeight(int previewHeight) {
            this.previewHeight = previewHeight;
        }

        public String getPreviewCaption() {
            return previewCaption;
        }

        public void setPreviewCaption(String previewCaption) { this.previewCaption = previewCaption; }

        public String getPreviewCaptionUrl() {
            return previewCaptionUrl;
        }

        public void setPreviewCaptionUrl(String previewCaptionUrl) {
            this.previewCaptionUrl = previewCaptionUrl;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public Bitmap getPreviewBitmap() {
            return previewBitmap;
        }

        public void setPreviewBitmap(Bitmap previewBitmap) {
            this.previewBitmap = previewBitmap;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(previewUrl);
            parcel.writeInt(previewWidth);
            parcel.writeInt(previewHeight);
            parcel.writeString(previewCaption);
            parcel.writeString(previewCaptionUrl);
            parcel.writeParcelable(previewBitmap, i);
        }
    }
}
