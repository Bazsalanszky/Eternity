package eu.toldi.infinityforlemmy.comment;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

import eu.toldi.infinityforlemmy.markdown.MarkdownUtils;
import eu.toldi.infinityforlemmy.utils.JSONUtils;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;


public class ParseComment {
    public static void parseComments(Executor executor, Handler handler, String response, Integer commentId,
                                     boolean expandChildren,
                                     ParseCommentListener parseCommentListener) {
        executor.execute(() -> {
            try {
                JSONArray childrenArray = new JSONObject(response).getJSONArray("comments");


                ArrayList<Comment> expandedNewComments = new ArrayList<>();
                ArrayList<Integer> moreChildrenIds = new ArrayList<>();


                Map<Integer, Comment> parsedComments = new HashMap<>();
                List<Comment> orderedComments = new ArrayList<>();
                List<Comment> topLevelComments = new ArrayList<>();
                for (int i = 0; i < childrenArray.length(); i++) {
                    Comment singleComment = parseSingleComment(childrenArray.getJSONObject(i));
                    orderedComments.add(singleComment);
                    parsedComments.put(singleComment.getId(), singleComment);
                    if (singleComment.getDepth() == 0) {
                        topLevelComments.add(singleComment);
                    }
                }
                Comment parentComment = (commentId != null) ? parsedComments.get(commentId) : null;
                if (parentComment != null) {
                    if (parentComment.getDepth() == 0) {
                        parentComment = null;
                    } else {
                        expandedNewComments.add(parentComment);
                    }
                }

                for (int i = orderedComments.size() - 1; i >= 0; i--) {
                    Comment c = orderedComments.get(i);
                    //Add children to parent
                    if (c.getParentId() != null) {
                        Comment parent = parsedComments.get(c.getParentId());
                        if (parent != null) {
                            parent.addChild(c);
                        }
                    }
                }

                //Add all comments to newComments
                ArrayList<Comment> newComments = new ArrayList<>(topLevelComments);

                expandChildren(newComments, expandedNewComments, expandChildren);

                if (topLevelComments.isEmpty() && !parsedComments.isEmpty() && parentComment != null) {
                    for (int i = 0; i < orderedComments.size(); i++) {
                        Comment c = orderedComments.get(i);
                        if (c.getParentId() == parentComment.getId())
                            expandedNewComments.add(c);
                    }
                }

                ArrayList<Comment> commentData;
                if (expandChildren) {
                    commentData = expandedNewComments;
                } else {
                    commentData = newComments;
                }

                handler.post(() -> parseCommentListener.onParseCommentSuccess(newComments, commentData, (newComments.size() == 0) ? null : newComments.get(0).getId(), moreChildrenIds));
            } catch (JSONException e) {
                e.printStackTrace();
                handler.post(parseCommentListener::onParseCommentFailed);
            }
        });
    }

    static void parseMoreComment(Executor executor, Handler handler, String response, boolean expandChildren,
                                 ParseCommentListener parseCommentListener) {
        executor.execute(() -> {
            try {
                JSONArray childrenArray = new JSONObject(response).getJSONArray("comments");

                ArrayList<Comment> newComments = new ArrayList<>();
                ArrayList<Comment> expandedNewComments = new ArrayList<>();
                ArrayList<Integer> moreChildrenIds = new ArrayList<>();

                // api response is a flat list of comments tree
                // process it in order and rebuild the tree
                for (int i = 0; i < childrenArray.length(); i++) {
                    JSONObject child = childrenArray.getJSONObject(i);
                    JSONObject childData = child.getJSONObject(JSONUtils.DATA_KEY);
                    if (child.getString(JSONUtils.KIND_KEY).equals(JSONUtils.KIND_VALUE_MORE)) {
                        Integer parentFullName = Integer.valueOf(childData.getString(JSONUtils.PARENT_ID_KEY));
                        JSONArray childrenIds = childData.getJSONArray(JSONUtils.CHILDREN_KEY);

                        if (childrenIds.length() != 0) {
                            ArrayList<Integer> localMoreChildrenIds = new ArrayList<>(childrenIds.length());
                            for (int j = 0; j < childrenIds.length(); j++) {
                                localMoreChildrenIds.add(childrenIds.getInt(j));
                            }

                            Comment parentComment = findCommentByFullName(newComments, parentFullName);
                            if (parentComment != null) {
                                parentComment.setHasReply(true);
                                parentComment.setMoreChildrenIds(localMoreChildrenIds);
                                parentComment.addChildren(new ArrayList<>()); // ensure children list is not null
                            } else {
                                // assume that it is parent of this call
                                moreChildrenIds.addAll(localMoreChildrenIds);
                            }
                        } else {
                            Comment parentComment = findCommentByFullName(newComments, parentFullName);
                            Comment continueThreadPlaceholder = new Comment(
                                    parentComment.getFullName(),
                                    childData.getInt(JSONUtils.DEPTH_KEY),
                                    Comment.PLACEHOLDER_CONTINUE_THREAD,
                                    parentComment.getId()
                            );


                            if (parentComment != null) {
                                parentComment.setHasReply(true);
                                parentComment.addChild(continueThreadPlaceholder, parentComment.getChildCount());
                                parentComment.setChildCount(parentComment.getChildCount());
                            } else {
                                // assume that it is parent of this call
                                newComments.add(continueThreadPlaceholder);
                            }
                        }
                    } else {
                        Comment comment = parseSingleComment(childData);
                        Integer parentFullName = comment.getParentId();

                        Comment parentComment = findCommentByFullName(newComments, parentFullName);
                        if (parentComment != null) {
                            parentComment.setHasReply(true);
                            parentComment.addChild(comment, parentComment.getChildCount());
                            parentComment.setChildCount(parentComment.getChildCount());
                        } else {
                            // assume that it is parent of this call
                            newComments.add(comment);
                        }
                    }
                }

                updateChildrenCount(newComments);
                expandChildren(newComments, expandedNewComments, expandChildren);

                ArrayList<Comment> commentData;
                if (expandChildren) {
                    commentData = expandedNewComments;
                } else {
                    commentData = newComments;
                }

                handler.post(() -> parseCommentListener.onParseCommentSuccess(newComments, commentData, null, moreChildrenIds));
            } catch (JSONException e) {
                e.printStackTrace();
                handler.post(parseCommentListener::onParseCommentFailed);
            }
        });
    }

    static void parseSentComment(Executor executor, Handler handler, String response,
                                 ParseSentCommentListener parseSentCommentListener) {
        executor.execute(() -> {
            try {
                JSONObject sentCommentData = new JSONObject(response).getJSONObject("comment_view");
                Comment comment = parseSingleComment(sentCommentData);

                handler.post(() -> parseSentCommentListener.onParseSentCommentSuccess(comment));
            } catch (JSONException e) {
                e.printStackTrace();
                String errorMessage = parseSentCommentErrorMessage(response);
                handler.post(() -> parseSentCommentListener.onParseSentCommentFailed(errorMessage));
            }
        });
    }

    private static void parseCommentRecursion(JSONArray comments, ArrayList<Comment> newCommentData,
                                              ArrayList<String> moreChildrenIds, int depth) throws JSONException {
        int actualCommentLength;

        if (comments.length() == 0) {
            return;
        }

        JSONObject more = comments.getJSONObject(comments.length() - 1).getJSONObject(JSONUtils.DATA_KEY);

        //Maybe moreChildrenIds contain only commentsJSONArray and no more info
        if (more.has(JSONUtils.COUNT_KEY)) {
            JSONArray childrenArray = more.getJSONArray(JSONUtils.CHILDREN_KEY);

            for (int i = 0; i < childrenArray.length(); i++) {
                moreChildrenIds.add(childrenArray.getString(i));
            }

            actualCommentLength = comments.length() - 1;

            if (moreChildrenIds.isEmpty() && comments.getJSONObject(comments.length() - 1).getString(JSONUtils.KIND_KEY).equals(JSONUtils.KIND_VALUE_MORE)) {
                //newCommentData.add(new Comment(more.getString(JSONUtils.PARENT_ID_KEY), more.getInt(JSONUtils.DEPTH_KEY), Comment.PLACEHOLDER_CONTINUE_THREAD));
                return;
            }
        } else {
            actualCommentLength = comments.length();
        }

        for (int i = 0; i < actualCommentLength; i++) {
            JSONObject data = comments.getJSONObject(i).getJSONObject(JSONUtils.DATA_KEY);
            Comment singleComment = parseSingleComment(data);
            newCommentData.add(singleComment);
        }
    }

    public static int getChildCount(Comment comment) {
        if (comment.getChildren() == null) {
            return 0;
        }
        int count = 0;
        for (Comment c : comment.getChildren()) {
            count += getChildCount(c);
        }
        return comment.getChildren().size() + count;
    }

    private static void expandChildren(ArrayList<Comment> comments, ArrayList<Comment> visibleComments,
                                       boolean setExpanded) {
        for (Comment c : comments) {
            visibleComments.add(c);
            if (c.hasReply()) {
                if (setExpanded) {
                    c.setExpanded(true);
                }
                expandChildren(c.getChildren(), visibleComments, setExpanded);
            } else {
                c.setExpanded(true);
            }
            if (c.getChildCount() > 0 && c.getChildCount() > getChildCount(c)) {
                //Add a load more placeholder
                Comment placeholder = new Comment(c.getFullName(), c.getDepth() + 1, Comment.PLACEHOLDER_LOAD_MORE_COMMENTS, c.getId());
                visibleComments.add(placeholder);
                c.addChild(placeholder, c.getChildren().size());
            }
        }
    }


    public static Comment parseSingleComment(JSONObject jsonObject) throws JSONException {
        JSONObject commentObj = jsonObject.getJSONObject("comment");
        JSONObject creatorObj = jsonObject.getJSONObject("creator");
        JSONObject postObj = jsonObject.getJSONObject("post");
        JSONObject communityObj = jsonObject.getJSONObject("community");
        JSONObject countsObj = jsonObject.getJSONObject("counts");

        int id = commentObj.getInt("id");
        int postID = postObj.getInt("id");
        String fullName = creatorObj.getString("name");
        String author = creatorObj.getString("name");
        String authorQualifiedName = LemmyUtils.actorID2FullName(creatorObj.getString("actor_id"));
        String linkAuthor = creatorObj.getString("actor_id");
        long commentTimeMillis = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            commentTimeMillis = ZonedDateTime.parse(commentObj.getString("published"),
                    DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("Z"))).toInstant().toEpochMilli();
        } else {
            String dateStr = commentObj.getString("published");

            dateStr = dateStr.substring(0, dateStr.lastIndexOf(".") + 4) + 'Z';
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date date = sdf.parse(dateStr);
                if (date != null) {
                    commentTimeMillis = date.getTime();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String content = MarkdownUtils.processImageCaptions(commentObj.getString("content"), "Image");
        String commentMarkdown = content;
        String commentRawText = content;
        String linkId = postObj.getString("id");
        String communityName = communityObj.getString("name");
        String communityQualifiedName = LemmyUtils.actorID2FullName(communityObj.getString("actor_id"));

        int score = countsObj.getInt("score");
        int voteType = (jsonObject.isNull("my_vote")) ? 0 : jsonObject.getInt("my_vote");
        if (voteType != 0)
            score -= 1;
        boolean isSubmitter = creatorObj.getInt("id") == postObj.getInt("creator_id");
        String distinguished = commentObj.getString("distinguished");
        String permalink = commentObj.getString("ap_id");
        String[] path = commentObj.getString("path").split(Pattern.quote("."));


        int depth = path.length - 2;

        Integer parentId = (depth > 0) ? Integer.valueOf(path[path.length - 2]) : null;
        boolean collapsed = false;
        boolean hasReply = countsObj.getInt("child_count") > 0;
        boolean saved = jsonObject.getBoolean("saved");
        long edited = 0;

        Comment comment = new Comment(id,postID, fullName, author, authorQualifiedName, linkAuthor, commentTimeMillis,
                commentMarkdown, commentRawText, linkId, communityName, communityQualifiedName, parentId,
                score, voteType, isSubmitter, distinguished, permalink, depth, collapsed, hasReply, saved, edited, path);
        int child_count = countsObj.getInt("child_count");
        comment.setChildCount(child_count);
        return comment;
    }


    @Nullable
    private static String parseSentCommentErrorMessage(String response) {
        try {
            JSONObject responseObject = new JSONObject(response).getJSONObject(JSONUtils.JSON_KEY);

            if (responseObject.getJSONArray(JSONUtils.ERRORS_KEY).length() != 0) {
                JSONArray error = responseObject.getJSONArray(JSONUtils.ERRORS_KEY)
                        .getJSONArray(responseObject.getJSONArray(JSONUtils.ERRORS_KEY).length() - 1);
                if (error.length() != 0) {
                    String errorString;
                    if (error.length() >= 2) {
                        errorString = error.getString(1);
                    } else {
                        errorString = error.getString(0);
                    }
                    return errorString.substring(0, 1).toUpperCase() + errorString.substring(1);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Nullable
    private static Comment findCommentByFullName(@NonNull List<Comment> comments, @NonNull Integer fullName) {
        for (Comment comment : comments) {
            if (comment.getId() == fullName &&
                    comment.getPlaceholderType() == Comment.NOT_PLACEHOLDER) {
                return comment;
            }
            if (comment.getChildren() != null) {
                Comment result = findCommentByFullName(comment.getChildren(), fullName);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static void updateChildrenCount(@NonNull List<Comment> comments) {
        for (Comment comment: comments) {
            comment.setChildCount(getChildCount(comment));
            if (comment.getChildren() != null) {
                updateChildrenCount(comment.getChildren());
            }
        }
    }

    public interface ParseCommentListener {
        void onParseCommentSuccess(ArrayList<Comment> topLevelComments, ArrayList<Comment> expandedComments, Integer parentId,
                                   ArrayList<Integer> moreChildrenIds);

        void onParseCommentFailed();
    }

    interface ParseSentCommentListener {
        void onParseSentCommentSuccess(Comment comment);

        void onParseSentCommentFailed(@Nullable String errorMessage);
    }
}
