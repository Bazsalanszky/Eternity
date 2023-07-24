package eu.toldi.infinityforlemmy.comment;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import eu.toldi.infinityforlemmy.BuildConfig;

public class Comment implements Parcelable {
    public static final int VOTE_TYPE_NO_VOTE = 0;
    public static final int VOTE_TYPE_UPVOTE = 1;
    public static final int VOTE_TYPE_DOWNVOTE = -1;
    public static final int NOT_PLACEHOLDER = 0;
    public static final int PLACEHOLDER_LOAD_MORE_COMMENTS = 1;
    public static final int PLACEHOLDER_CONTINUE_THREAD = 2;
    public static final Creator<Comment> CREATOR = new Creator<>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
    private int id;
    private String fullName;
    private String author;
    private String authorQualifiedName;
    private String authorIconUrl;
    private String linkAuthor;
    private long commentTimeMillis;
    private String commentMarkdown;
    private String commentRawText;
    private String linkId;
    private String communityName;

    private String communityQualifiedName;
    private Integer parentId;
    private int score;
    private int voteType;
    private boolean isSubmitter;
    private String distinguished;
    private String permalink;
    private int depth;
    private int childCount;
    private boolean collapsed;
    private boolean hasReply;
    private boolean saved;
    private boolean isExpanded;
    private boolean hasExpandedBefore;
    private ArrayList<Comment> children = new ArrayList<>();
    private ArrayList<Integer> moreChildrenIds;
    private int placeholderType;
    private boolean isLoadingMoreChildren;
    private boolean loadMoreChildrenFailed;
    private long editedTimeMillis;

    private String[] path;

    public Comment(int id, String fullName, String author, String authorQualifiedName, String linkAuthor,
                   long commentTimeMillis, String commentMarkdown, String commentRawText,
                   String linkId, String communityName, String communityQualifiedName, Integer parentId, int score,
                   int voteType, boolean isSubmitter, String distinguished, String permalink,
                   int depth, boolean collapsed, boolean hasReply, boolean saved, long edited, String[] path) {
        this.id = id;
        this.fullName = fullName;
        this.author = author;
        this.authorQualifiedName = authorQualifiedName;
        this.linkAuthor = linkAuthor;
        this.commentTimeMillis = commentTimeMillis;
        this.commentMarkdown = commentMarkdown;
        this.commentRawText = commentRawText;
        this.linkId = linkId;
        this.communityName = communityName;
        this.communityQualifiedName = communityQualifiedName;
        this.parentId = parentId;
        this.score = score;
        this.voteType = voteType;
        this.isSubmitter = isSubmitter;
        this.distinguished = distinguished;
        this.permalink = permalink;
        this.depth = depth;
        this.collapsed = collapsed;
        this.hasReply = hasReply;
        this.saved = saved;
        this.isExpanded = false;
        this.hasExpandedBefore = false;
        this.editedTimeMillis = edited;
        this.path = path;
        placeholderType = NOT_PLACEHOLDER;
    }

    public Comment(String parentFullName, int depth, int placeholderType, Integer parentId) {
        if (placeholderType == PLACEHOLDER_LOAD_MORE_COMMENTS) {
            this.fullName = parentFullName;
        } else {
            this.fullName = parentFullName;
            this.parentId = parentId;
        }
        this.depth = depth;
        this.placeholderType = placeholderType;
        isLoadingMoreChildren = false;
        loadMoreChildrenFailed = false;
    }

    public Comment(String parentFullName) {

    }

    protected Comment(Parcel in) {
        id = in.readInt();
        fullName = in.readString();
        author = in.readString();
        authorQualifiedName = in.readString();
        authorIconUrl = in.readString();
        linkAuthor = in.readString();
        commentTimeMillis = in.readLong();
        commentMarkdown = in.readString();
        commentRawText = in.readString();
        linkId = in.readString();
        communityName = in.readString();
        communityQualifiedName = in.readString();
        parentId = in.readInt();
        score = in.readInt();
        voteType = in.readInt();
        isSubmitter = in.readByte() != 0;
        distinguished = in.readString();
        permalink = in.readString();
        depth = in.readInt();
        childCount = in.readInt();
        collapsed = in.readByte() != 0;
        hasReply = in.readByte() != 0;
        isExpanded = in.readByte() != 0;
        hasExpandedBefore = in.readByte() != 0;
        children = new ArrayList<>();
        in.readTypedList(children, Comment.CREATOR);
        moreChildrenIds = new ArrayList<>();
        List<String> childrenIDs = new ArrayList<>();
        in.readStringList(childrenIDs);
        for (int i = 0; i < childrenIDs.size(); i++) {
            moreChildrenIds.add(Integer.valueOf(childrenIDs.get(i)));
        }
        placeholderType = in.readInt();
        isLoadingMoreChildren = in.readByte() != 0;
        loadMoreChildrenFailed = in.readByte() != 0;
        in.readStringArray(path);
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAuthorDeleted() {
        return author != null && author.equals("[deleted]");
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public String getAuthorIconUrl() {
        return authorIconUrl;
    }

    public void setAuthorIconUrl(String authorIconUrl) {
        this.authorIconUrl = authorIconUrl;
    }

    public String getLinkAuthor() {
        return linkAuthor;
    }

    public long getCommentTimeMillis() {
        return commentTimeMillis;
    }

    public String getCommentMarkdown() {
        return commentMarkdown;
    }

    public void setCommentMarkdown(String commentMarkdown) {
        this.commentMarkdown = commentMarkdown;
    }

    public String getCommentRawText() {
        return commentRawText;
    }

    public void setCommentRawText(String commentRawText) {
        this.commentRawText = commentRawText;
    }

    public String getLinkId() {
        return linkId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isSubmitter() {
        return isSubmitter;
    }

    public void setSubmittedByAuthor(boolean isSubmittedByAuthor) {
        this.isSubmitter = isSubmittedByAuthor;
    }

    public boolean isModerator() {
        return distinguished != null && distinguished.equals("moderator");
    }

    public boolean isAdmin() {
        return distinguished != null && distinguished.equals("admin");
    }

    public String getPermalink() {
        return permalink;
    }

    public int getDepth() {
        return depth;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public boolean hasReply() {
        return hasReply;
    }

    public void setHasReply(boolean hasReply) {
        this.hasReply = hasReply;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
        if (isExpanded && !hasExpandedBefore) {
            hasExpandedBefore = true;
        }
    }

    public boolean hasExpandedBefore() {
        return hasExpandedBefore;
    }

    public int getVoteType() {
        return voteType;
    }

    public void setVoteType(int voteType) {
        this.voteType = voteType;
    }

    public ArrayList<Comment> getChildren() {
        return children;
    }

    public void addChildren(ArrayList<Comment> moreChildren) {
        if (children == null || children.size() == 0) {
            children = moreChildren;
        } else {
            if (children.size() > 1 && children.get(children.size() - 1).placeholderType == PLACEHOLDER_LOAD_MORE_COMMENTS) {
                for (int i = 0; i < moreChildren.size(); i++) {
                    boolean found = false;
                    for (int j = 0; j < children.size(); j++) {
                        if (children.get(j).id == moreChildren.get(i).id) {
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                        children.add(moreChildren.get(i));
                }

            } else {
                // Add only unique children
                for (int i = 0; i < moreChildren.size(); i++) {
                    boolean found = false;
                    for (int j = 0; j < children.size(); j++) {
                        if (children.get(j).id == moreChildren.get(i).id) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        children.add(moreChildren.get(i));
                    }
                }
            }
        }
        //childCount += moreChildren == null ? 0 : moreChildren.size();
        assertChildrenDepth();
    }

    public void addChild(Comment comment) {
        addChild(comment, 0);
        //childCount++;
        assertChildrenDepth();
    }

    public void addChild(Comment comment, int position) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(position, comment);
        assertChildrenDepth();
    }

    private void assertChildrenDepth() {
        if (BuildConfig.DEBUG) {
            for (Comment child: children) {
                if (child.depth != depth + 1) {
                    throw new IllegalStateException("Child depth is not one more than parent depth");
                }
            }
        }
    }

    public ArrayList<Integer> getMoreChildrenIds() {
        return moreChildrenIds;
    }

    public void setMoreChildrenIds(ArrayList<Integer> moreChildrenIds) {
        this.moreChildrenIds = moreChildrenIds;
    }

    public boolean hasMoreChildrenIds() {
        return moreChildrenIds != null;
    }

    public void removeMoreChildrenIds() {
        moreChildrenIds.clear();
    }

    public int getPlaceholderType() {
        return placeholderType;
    }

    public boolean isLoadingMoreChildren() {
        return isLoadingMoreChildren;
    }

    public void setLoadingMoreChildren(boolean isLoadingMoreChildren) {
        this.isLoadingMoreChildren = isLoadingMoreChildren;
    }

    public boolean isLoadMoreChildrenFailed() {
        return loadMoreChildrenFailed;
    }

    public void setLoadMoreChildrenFailed(boolean loadMoreChildrenFailed) {
        this.loadMoreChildrenFailed = loadMoreChildrenFailed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(fullName);
        parcel.writeString(author);
        parcel.writeString(authorQualifiedName);
        parcel.writeString(authorIconUrl);
        parcel.writeString(linkAuthor);
        parcel.writeLong(commentTimeMillis);
        parcel.writeString(commentMarkdown);
        parcel.writeString(commentRawText);
        parcel.writeString(linkId);
        parcel.writeString(communityName);
        parcel.writeString(communityQualifiedName);
        parcel.writeInt(parentId == null ? 0 : parentId);
        parcel.writeInt(score);
        parcel.writeInt(voteType);
        parcel.writeByte((byte) (isSubmitter ? 1 : 0));
        parcel.writeString(distinguished);
        parcel.writeString(permalink);
        parcel.writeInt(depth);
        parcel.writeInt(childCount);
        parcel.writeByte((byte) (collapsed ? 1 : 0));
        parcel.writeByte((byte) (hasReply ? 1 : 0));
        parcel.writeByte((byte) (isExpanded ? 1 : 0));
        parcel.writeByte((byte) (hasExpandedBefore ? 1 : 0));
        parcel.writeTypedList(children);
        List<String> childrenIds = new ArrayList<>();
        if (moreChildrenIds != null) {
            for (int j = 0; j < moreChildrenIds.size(); j++) {
                childrenIds.add(String.valueOf(moreChildrenIds.get(i)));
            }
        }
        parcel.writeStringList(childrenIds);
        parcel.writeInt(placeholderType);
        parcel.writeByte((byte) (isLoadingMoreChildren ? 1 : 0));
        parcel.writeByte((byte) (loadMoreChildrenFailed ? 1 : 0));
        parcel.writeStringArray(path);
    }

    public String[] getPath() {
        return path;
    }

    public boolean isEdited() {
        return editedTimeMillis != 0;
    }

    public long getEditedTimeMillis() {
        return editedTimeMillis;
    }

    public String getAuthorQualifiedName() {
        return authorQualifiedName;
    }

    public String getCommunityQualifiedName() {
        return communityQualifiedName;
    }
}
