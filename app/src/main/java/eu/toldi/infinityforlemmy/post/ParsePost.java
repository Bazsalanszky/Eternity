package eu.toldi.infinityforlemmy.post;

import static java.lang.Integer.max;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.toldi.infinityforlemmy.community.BasicCommunityInfo;
import eu.toldi.infinityforlemmy.post.enrich.PostEnricher;
import eu.toldi.infinityforlemmy.postfilter.PostFilter;
import eu.toldi.infinityforlemmy.user.BasicUserInfo;
import eu.toldi.infinityforlemmy.utils.JSONUtils;
import eu.toldi.infinityforlemmy.utils.LemmyUtils;
import eu.toldi.infinityforlemmy.utils.Utils;

/**
 * Created by alex on 3/21/18.
 */

public class ParsePost {
    public static LinkedHashSet<Post> parsePostsSync(String response, int nPosts, PostFilter postFilter,
                                                     List<String> readPostList, PostEnricher postEnricher) {
        LinkedHashSet<Post> newPosts = new LinkedHashSet<>();
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray allPosts = jsonResponse.getJSONArray("posts");

            //Posts listing
            int size;
            if (nPosts < 0 || nPosts > allPosts.length()) {
                size = allPosts.length();
            } else {
                size = nPosts;
            }

            HashSet<String> readPostHashSet = null;
            if (readPostList != null) {
                readPostHashSet = new HashSet<>(readPostList);
            }
            for (int i = 0; i < size; i++) {
                try {

                    JSONObject data = allPosts.getJSONObject(i);
                    Post post = parseBasicData(data);
                    if (readPostHashSet != null && readPostHashSet.contains(String.valueOf(post.getId()))) {
                        post.markAsRead();
                    }
                    if (PostFilter.isPostAllowed(post, postFilter)) {
                        newPosts.add(post);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            postEnricher.enrich(newPosts);

            return newPosts;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void parsePost(Executor executor, Handler handler, PostEnricher postEnricher,
                                 String response, ParsePostListener parsePostListener) {
        executor.execute(() -> {
            try {
                JSONObject allData = new JSONObject(response).getJSONObject("post_view");
                if (allData.length() == 0) {
                    handler.post(parsePostListener::onParsePostFail);
                    return;
                }

                Post post = parseBasicData(allData);
                postEnricher.enrich(List.of(post));
                handler.post(() -> parsePostListener.onParsePostSuccess(post));
            } catch (JSONException e) {
                e.printStackTrace();
                handler.post(parsePostListener::onParsePostFail);
            }
        });
    }

    public static void parseRandomPost(Executor executor, Handler handler, String response, boolean isNSFW,
                                       ParseRandomPostListener parseRandomPostListener) {
        executor.execute(() -> {
            try {
                JSONArray postsArray = new JSONObject(response).getJSONObject(JSONUtils.DATA_KEY).getJSONArray(JSONUtils.CHILDREN_KEY);
                if (postsArray.length() == 0) {
                    handler.post(parseRandomPostListener::onParseRandomPostFailed);
                } else {
                    JSONObject post = postsArray.getJSONObject(0).getJSONObject(JSONUtils.DATA_KEY);
                    String subredditName = post.getString(JSONUtils.SUBREDDIT_KEY);
                    String postId;
                    if (isNSFW) {
                        postId = post.getString(JSONUtils.ID_KEY);
                    } else {
                        postId = post.getString(JSONUtils.LINK_ID_KEY).substring("t3_".length());
                    }
                    handler.post(() -> parseRandomPostListener.onParseRandomPostSuccess(postId, subredditName));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                handler.post(parseRandomPostListener::onParseRandomPostFailed);
            }
        });
    }

    public static Post parseBasicData(JSONObject data) throws JSONException {
        JSONObject post = data.getJSONObject("post");
        JSONObject creator = data.getJSONObject("creator");
        JSONObject community = data.getJSONObject("community");
        JSONObject counts = data.getJSONObject("counts");
        boolean isModerator = data.getBoolean("creator_is_moderator");
        boolean isAdmin = creator.optBoolean("admin") || data.optBoolean("creator_is_admin");

        int id = post.getInt("id");
        String fullName = post.getString("name");
        String subredditName = community.getString("name");
        String subredditNamePrefixed = LemmyUtils.actorID2FullName(community.getString("actor_id"));
        String author = creator.getString("name");
        String authorFull = LemmyUtils.actorID2FullName(creator.getString("actor_id"));
        long postTimeMillis = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            postTimeMillis = ZonedDateTime.parse(post.getString("published"),
                    DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("Z"))).toInstant().toEpochMilli();
        } else {
            String dateStr = post.getString("published");

            dateStr = dateStr.substring(0, dateStr.lastIndexOf(".") + 4) + 'Z';
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date date = sdf.parse(dateStr);
                if (date != null) {
                    postTimeMillis = date.getTime();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String title = post.getString("name").replace("&amp;", "&");
        String permalink = post.getString("ap_id");
        int score = counts.getInt("score");
        int upvotes = counts.getInt("upvotes");
        int downvotes = counts.getInt("downvotes");
        int voteType = 0;
        int nComments = counts.getInt("comments");
        int upvoteRatio = 100 * counts.getInt("upvotes") / max(counts.getInt("upvotes") + counts.getInt("downvotes"), 1);
        boolean hidden = community.getBoolean("hidden");
        boolean nsfw = post.getBoolean("nsfw");
        boolean locked = post.getBoolean("locked");
        boolean saved = data.getBoolean("saved");
        boolean deleted = post.getBoolean("deleted");

        String suggestedSort = "";
        ArrayList<Post.Preview> previews = new ArrayList<>();
        if (!post.isNull("thumbnail_url")) {
            String thumbnail = post.getString("thumbnail_url");
            //int[] wh_array = getImageDimension(thumbnail);
            previews.add(new Post.Preview(thumbnail, 0, 0, "", ""));
        }
        BasicUserInfo authorInfo = new BasicUserInfo(creator.getInt("id"), author, authorFull, creator.optString("avatar", ""), creator.optString("display_name", author));
        BasicCommunityInfo communityInfo = new BasicCommunityInfo(community.getInt("id"), subredditName, subredditNamePrefixed, community.optString("icon", ""), community.optString("title", subredditName));

        return parseData(data, permalink, id, communityInfo,
                authorInfo, postTimeMillis, title, previews,
                downvotes, upvotes, voteType, nComments, upvoteRatio, nsfw, locked, saved, deleted,
                isModerator, isAdmin, suggestedSort);

    }

    private static Post parseData(JSONObject data, String permalink, int id, BasicCommunityInfo communityInfo, BasicUserInfo author,
                                  long postTimeMillis, String title, ArrayList<Post.Preview> previews,
                                  int downvotes, int upvotes, int voteType, int nComments, int upvoteRatio,
                                  boolean nsfw, boolean locked,
                                  boolean saved, boolean deleted,
                                  boolean isModerator, boolean isAdmin, String suggestedSort) throws JSONException {
        Post post;


        String url = (!data.getJSONObject("post").isNull("url")) ? data.getJSONObject("post").getString("url") : "";
        String communityURL = (!data.getJSONObject("community").isNull("icon")) ? data.getJSONObject("community").getString("icon") : "";
        String authorAvatar = (!data.getJSONObject("creator").isNull("avatar")) ? data.getJSONObject("creator").getString("avatar") : null;

        Uri uri = Uri.parse(url);
        String path = uri.getPath();
        boolean isVideo = path.endsWith(".mp4") || path.endsWith(".webm") || path.endsWith(".gifv");

        if (!data.getJSONObject("post").has("thumbnail_url") && previews.isEmpty()) {
            if (!data.getJSONObject("post").isNull("body") && url.equals("")) {
                //Text post
                int postType = Post.TEXT_TYPE;
                post = new Post(id, communityInfo, author,
                        postTimeMillis, title, permalink, downvotes, upvotes,
                        postType, voteType, nComments, upvoteRatio, nsfw,
                        locked, saved, deleted, isModerator, isAdmin, suggestedSort);
                String body = data.getJSONObject("post").getString("body");
                post.setSelfText(body);
                post.setSelfTextPlain(body);
                post.setSelfTextPlainTrimmed(body.trim());
            } else {
                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".jpeg") || path.endsWith(".webp")) {
                    //Image post
                    int postType = Post.IMAGE_TYPE;

                    post = new Post(id, communityInfo, author,
                            postTimeMillis, title, url, permalink, downvotes, upvotes,
                            postType, voteType, nComments, upvoteRatio, nsfw, locked, saved, deleted, isModerator, isAdmin, suggestedSort);

                    if (previews.isEmpty()) {
                        previews.add(new Post.Preview(url, 0, 0, "", ""));
                    }
                    post.setPreviews(previews);
                } else {
                    if (isVideo) {
                        //No preview video post
                        int postType = Post.VIDEO_TYPE;

                        post = new Post(id, communityInfo, author, postTimeMillis, title, permalink, downvotes, upvotes, postType, voteType,
                                nComments, upvoteRatio, nsfw, locked, saved, deleted, isModerator, isAdmin, suggestedSort);
                        Post.Preview preview = new Post.Preview(url, 0, 0, "", "");
                        post.setPreviews(new ArrayList<>(List.of(preview)));
                        post.setVideoUrl(url);
                        post.setVideoDownloadUrl(url);
                    } else if (!url.equals("")) {
                        //No preview link post
                        int postType = Post.NO_PREVIEW_LINK_TYPE;
                        post = new Post(id, communityInfo, author,
                                postTimeMillis, title, url, permalink, downvotes, upvotes,
                                postType, voteType, nComments, upvoteRatio, nsfw, locked, saved, deleted, isModerator, isAdmin, suggestedSort);
                        if (data.isNull(JSONUtils.SELFTEXT_KEY)) {
                            post.setSelfText("");
                        } else {
                            post.setSelfText(Utils.modifyMarkdown(Utils.trimTrailingWhitespace(data.getString(JSONUtils.SELFTEXT_KEY))));
                        }

                        String authority = uri.getAuthority();

                        if (authority != null) {
                            if (authority.contains("redgifs.com")) {
                                String redgifsId = url.substring(url.lastIndexOf("/") + 1).toLowerCase();
                                post.setPostType(Post.VIDEO_TYPE);
                                post.setIsRedgifs(true);
                                post.setVideoUrl(url);
                                post.setRedgifsId(redgifsId);
                            } else if (authority.equals("streamable.com")) {
                                String shortCode = url.substring(url.lastIndexOf("/") + 1);
                                post.setPostType(Post.VIDEO_TYPE);
                                post.setIsStreamable(true);
                                post.setVideoUrl(url);
                                post.setStreamableShortCode(shortCode);
                            }
                        }
                    } else {
                        int postType = Post.TEXT_TYPE;
                        post = new Post(id, communityInfo, author,
                                postTimeMillis, title, permalink, downvotes, upvotes,
                                postType, voteType, nComments, upvoteRatio, nsfw,
                                locked, saved, deleted, isModerator, isAdmin, suggestedSort);
                        String body = "";
                        post.setSelfText(body);
                        post.setSelfTextPlain(body);
                        post.setSelfTextPlainTrimmed(body.trim());
                    }
                }
            }
        } else {
            if (previews.isEmpty()) {
                if (data.getJSONObject("post").has("thumbnail_url")) {
                    JSONObject images = data.getJSONObject(JSONUtils.PREVIEW_KEY).getJSONArray(JSONUtils.IMAGES_KEY).getJSONObject(0);
                    String previewUrl = images.getJSONObject(JSONUtils.SOURCE_KEY).getString(JSONUtils.URL_KEY);
                    int previewWidth = images.getJSONObject(JSONUtils.SOURCE_KEY).getInt(JSONUtils.WIDTH_KEY);
                    int previewHeight = images.getJSONObject(JSONUtils.SOURCE_KEY).getInt(JSONUtils.HEIGHT_KEY);
                    previews.add(new Post.Preview(previewUrl, previewWidth, previewHeight, "", ""));

                    JSONArray thumbnailPreviews = images.getJSONArray(JSONUtils.RESOLUTIONS_KEY);
                    for (int i = 0; i < thumbnailPreviews.length(); i++) {
                        JSONObject thumbnailPreview = images.getJSONArray(JSONUtils.RESOLUTIONS_KEY).getJSONObject(i);
                        String thumbnailPreviewUrl = thumbnailPreview.getString(JSONUtils.URL_KEY);
                        int thumbnailPreviewWidth = thumbnailPreview.getInt(JSONUtils.WIDTH_KEY);
                        int thumbnailPreviewHeight = thumbnailPreview.getInt(JSONUtils.HEIGHT_KEY);

                        previews.add(new Post.Preview(thumbnailPreviewUrl, thumbnailPreviewWidth, thumbnailPreviewHeight, "", ""));
                    }
                }
            }

            if (isVideo) {
                //Video post
                JSONObject redditVideoObject = data.getJSONObject(JSONUtils.MEDIA_KEY).getJSONObject(JSONUtils.REDDIT_VIDEO_KEY);
                int postType = Post.VIDEO_TYPE;
                String videoUrl = Html.fromHtml(redditVideoObject.getString(JSONUtils.HLS_URL_KEY)).toString();
                String videoDownloadUrl = redditVideoObject.getString(JSONUtils.FALLBACK_URL_KEY);

                post = new Post(id, communityInfo, author, postTimeMillis, title, permalink, downvotes, upvotes, postType, voteType,
                        nComments, upvoteRatio, nsfw, locked, saved, deleted, isModerator, isAdmin, suggestedSort);

                post.setPreviews(previews);
                post.setVideoUrl(videoUrl);
                post.setVideoDownloadUrl(videoDownloadUrl);
            } else if (data.getJSONObject("post").has("thumbnail_url")) {
                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".jpeg") || path.endsWith(".webp")) {
                    //Image post
                    int postType = Post.IMAGE_TYPE;

                    post = new Post(id, communityInfo, author,
                            postTimeMillis, title, url, permalink, downvotes, upvotes,
                            postType, voteType, nComments, upvoteRatio, nsfw, locked, saved, deleted,
                            isModerator, isAdmin, suggestedSort);

                    if (previews.isEmpty()) {
                        previews.add(new Post.Preview(url, 0, 0, "", ""));
                    }
                    post.setPreviews(previews);
                } else if (path.endsWith(".gif")) {
                    //Gif post
                    int postType = Post.GIF_TYPE;
                    post = new Post(id, communityInfo, author,
                            postTimeMillis, title, url, permalink, downvotes, upvotes,
                            postType, voteType, nComments, upvoteRatio,
                            nsfw, locked, saved, deleted,
                            isModerator, isAdmin, suggestedSort);

                    post.setPreviews(previews);
                    post.setVideoUrl(url);
                } else if (uri.getAuthority() != null && uri.getAuthority().contains("imgur.com") && (path.endsWith(".gifv") || path.endsWith(".mp4"))) {
                    // Imgur gifv/mp4
                    int postType = Post.VIDEO_TYPE;

                    if (url.endsWith("gifv")) {
                        url = url.substring(0, url.length() - 5) + ".mp4";
                    }

                    post = new Post(id, communityInfo, author,
                            postTimeMillis, title, url, permalink, downvotes, upvotes,
                            postType, voteType, nComments, upvoteRatio,
                            nsfw, locked, saved, deleted,
                            isModerator, isAdmin, suggestedSort);
                    post.setPreviews(previews);
                    post.setVideoUrl(url);
                    post.setVideoDownloadUrl(url);
                    post.setIsImgur(true);
                } else if (path.endsWith(".mp4")) {
                    //Video post
                    int postType = Post.VIDEO_TYPE;

                    post = new Post(id, communityInfo, author,
                            postTimeMillis, title, url, permalink, downvotes, upvotes,
                            postType, voteType, nComments, upvoteRatio, nsfw, locked, saved, deleted,
                            isModerator, isAdmin, suggestedSort);
                    post.setPreviews(previews);
                    post.setVideoUrl(url);
                    post.setVideoDownloadUrl(url);
                } else {
                    //Link post
                    int postType = Post.LINK_TYPE;

                    post = new Post(id, communityInfo, author,
                            postTimeMillis, title, url, permalink, downvotes, upvotes,
                            postType, voteType, nComments, upvoteRatio, nsfw, locked, saved, deleted,
                            isModerator, isAdmin, suggestedSort);
                    if (data.isNull(JSONUtils.SELFTEXT_KEY)) {
                        post.setSelfText("");
                    } else {
                        post.setSelfText(Utils.modifyMarkdown(Utils.trimTrailingWhitespace(data.getString(JSONUtils.SELFTEXT_KEY))));
                    }

                    post.setPreviews(previews);

                    String authority = uri.getAuthority();

                    if (authority != null) {
                        if (authority.contains("redgifs.com")) {
                            String redgifsId = url.substring(url.lastIndexOf("/") + 1).toLowerCase();
                            post.setPostType(Post.VIDEO_TYPE);
                            post.setIsRedgifs(true);
                            post.setVideoUrl(url);
                            post.setRedgifsId(redgifsId);
                        } else if (authority.equals("streamable.com")) {
                            String shortCode = url.substring(url.lastIndexOf("/") + 1);
                            post.setPostType(Post.VIDEO_TYPE);
                            post.setIsStreamable(true);
                            post.setVideoUrl(url);
                            post.setStreamableShortCode(shortCode);
                        }
                    }
                }
            } else {
                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".jpeg")) {
                    //Image post
                    int postType = Post.IMAGE_TYPE;

                    post = new Post(id, communityInfo, author,
                            postTimeMillis, title, url, permalink, downvotes, upvotes,
                            postType, voteType, nComments, upvoteRatio, nsfw, locked, saved, deleted, isModerator, isAdmin, suggestedSort);

                    if (previews.isEmpty()) {
                        previews.add(new Post.Preview(url, 0, 0, "", ""));
                    }
                    post.setPreviews(previews);
                } else if (path.endsWith(".mp4")) {
                    //Video post
                    int postType = Post.VIDEO_TYPE;

                    post = new Post(id, communityInfo, author,
                            postTimeMillis, title, url, permalink, downvotes, upvotes,
                            postType, voteType, nComments, upvoteRatio, nsfw, locked, saved, deleted, isModerator, isAdmin, suggestedSort);
                    post.setPreviews(previews);
                    post.setVideoUrl(url);
                    post.setVideoDownloadUrl(url);
                } else {
                    //CP No Preview Link post
                    int postType = Post.NO_PREVIEW_LINK_TYPE;

                    post = new Post(id, communityInfo, author,
                            postTimeMillis, title, url, permalink, downvotes, upvotes,
                            postType, voteType, nComments, upvoteRatio, nsfw, locked, saved, deleted, isModerator, isAdmin, suggestedSort);
                    //Need attention
                    if (data.isNull(JSONUtils.SELFTEXT_KEY)) {
                        post.setSelfText("");
                    } else {
                        post.setSelfText(Utils.modifyMarkdown(Utils.trimTrailingWhitespace(data.getString(JSONUtils.SELFTEXT_KEY))));
                    }

                    String authority = uri.getAuthority();

                    if (authority != null) {
                        if (authority.contains("redgifs.com")) {
                            String redgifsId = url.substring(url.lastIndexOf("/") + 1).toLowerCase();
                            post.setPostType(Post.VIDEO_TYPE);
                            post.setIsRedgifs(true);
                            post.setVideoUrl(url);
                            post.setRedgifsId(redgifsId);
                        } else if (authority.equals("streamable.com")) {
                            String shortCode = url.substring(url.lastIndexOf("/") + 1);
                            post.setPostType(Post.VIDEO_TYPE);
                            post.setIsStreamable(true);
                            post.setVideoUrl(url);
                            post.setStreamableShortCode(shortCode);
                        }
                    }
                }
            }
        }

        if (post.getPostType() == Post.VIDEO_TYPE) {
            try {
                String authority = uri.getAuthority();
                if (authority != null) {
                    if (authority.contains("redgifs.com")) {
                        String redgifsId = url.substring(url.lastIndexOf("/") + 1);
                        if (redgifsId.contains("-")) {
                            redgifsId = redgifsId.substring(0, redgifsId.indexOf('-'));
                        }
                        post.setIsRedgifs(true);
                        post.setVideoUrl(url);
                        post.setRedgifsId(redgifsId.toLowerCase());
                    } else if (authority.equals("streamable.com")) {
                        String shortCode = url.substring(url.lastIndexOf("/") + 1);
                        post.setPostType(Post.VIDEO_TYPE);
                        post.setIsStreamable(true);
                        post.setVideoUrl(url);
                        post.setStreamableShortCode(shortCode);
                    }
                }
            } catch (IllegalArgumentException ignore) {
            }
        } else if (post.getPostType() == Post.TEXT_TYPE) {
            List<ImageInfo> images = extractImages(post.getSelfText());
            if (images.size() == 1) {
                post.setPostType(Post.IMAGE_TYPE);
                post.setUrl(images.get(0).imageUrl);
            } else if (images.size() > 1) {
                post.setPostType(Post.GALLERY_TYPE);
                ArrayList<Post.Gallery> gallery = new ArrayList<>();
                for (ImageInfo image : images) {
                    String[] imagePath = image.getImageUrl().split(Pattern.quote("/"));
                    gallery.add(new Post.Gallery("image/jpg", image.getImageUrl(), "", imagePath[imagePath.length - 1], image.caption, ""));
                }
                post.setGallery(gallery);
            }

            if (!data.isNull(JSONUtils.GALLERY_DATA_KEY)) {
                JSONArray galleryIdsArray = data.getJSONObject(JSONUtils.GALLERY_DATA_KEY).getJSONArray(JSONUtils.ITEMS_KEY);
                JSONObject galleryObject = data.getJSONObject(JSONUtils.MEDIA_METADATA_KEY);
                ArrayList<Post.Gallery> gallery = new ArrayList<>();
                for (int i = 0; i < galleryIdsArray.length(); i++) {
                    String galleryId = galleryIdsArray.getJSONObject(i).getString(JSONUtils.MEDIA_ID_KEY);
                    JSONObject singleGalleryObject = galleryObject.getJSONObject(galleryId);
                    String mimeType = singleGalleryObject.getString(JSONUtils.M_KEY);
                    String galleryItemUrl;
                    if (mimeType.contains("jpg") || mimeType.contains("png")) {
                        galleryItemUrl = singleGalleryObject.getJSONObject(JSONUtils.S_KEY).getString(JSONUtils.U_KEY);
                    } else {
                        JSONObject sourceObject = singleGalleryObject.getJSONObject(JSONUtils.S_KEY);
                        if (mimeType.contains("gif")) {
                            galleryItemUrl = sourceObject.getString(JSONUtils.GIF_KEY);
                        } else {
                            galleryItemUrl = sourceObject.getString(JSONUtils.MP4_KEY);
                        }
                    }

                    JSONObject galleryItem = galleryIdsArray.getJSONObject(i);
                    String galleryItemCaption = "";
                    String galleryItemCaptionUrl = "";
                    if (galleryItem.has(JSONUtils.CAPTION_KEY)) {
                        galleryItemCaption = galleryItem.getString(JSONUtils.CAPTION_KEY).trim();
                    }

                    if (galleryItem.has(JSONUtils.CAPTION_URL_KEY)) {
                        galleryItemCaptionUrl = galleryItem.getString(JSONUtils.CAPTION_URL_KEY).trim();
                    }

                    if (previews.isEmpty() && (mimeType.contains("jpg") || mimeType.contains("png"))) {
                        previews.add(new Post.Preview(galleryItemUrl, singleGalleryObject.getJSONObject(JSONUtils.S_KEY).getInt(JSONUtils.X_KEY),
                                singleGalleryObject.getJSONObject(JSONUtils.S_KEY).getInt(JSONUtils.Y_KEY), galleryItemCaption, galleryItemCaptionUrl));
                    }

                    Post.Gallery postGalleryItem = new Post.Gallery(mimeType, galleryItemUrl, "", communityInfo.getDisplayName() + "-" + galleryId + "." + mimeType.substring(mimeType.lastIndexOf("/") + 1), galleryItemCaption, galleryItemCaptionUrl);

                    // For issue #558
                    // Construct a fallback image url
                    if (!TextUtils.isEmpty(galleryItemUrl) && !TextUtils.isEmpty(mimeType) && (mimeType.contains("jpg") || mimeType.contains("png"))) {
                        postGalleryItem.setFallbackUrl("https://i.redd.it/" + galleryId + "." +  mimeType.substring(mimeType.lastIndexOf("/") + 1));
                        postGalleryItem.setHasFallback(true);
                    }

                    gallery.add(postGalleryItem);
                }

                if (!gallery.isEmpty()) {
                    post.setPostType(Post.GALLERY_TYPE);
                    post.setGallery(gallery);
                    post.setPreviews(previews);
                }
            } else if (post.getPostType() == Post.LINK_TYPE) {
                String authority = uri.getAuthority();

                if (authority != null) {
                    if (authority.contains("redgifs.com")) {
                        String redgifsId = url.substring(url.lastIndexOf("/") + 1).toLowerCase();
                        post.setPostType(Post.VIDEO_TYPE);
                        post.setIsRedgifs(true);
                        post.setVideoUrl(url);
                        post.setRedgifsId(redgifsId);
                    } else if (authority.equals("streamable.com")) {
                        String shortCode = url.substring(url.lastIndexOf("/") + 1);
                        post.setPostType(Post.VIDEO_TYPE);
                        post.setIsStreamable(true);
                        post.setVideoUrl(url);
                        post.setStreamableShortCode(shortCode);
                    }
                }
            }
        }

        if (post.getPostType() != Post.LINK_TYPE && post.getPostType() != Post.NO_PREVIEW_LINK_TYPE) {
            if (data.isNull(JSONUtils.SELFTEXT_KEY)) {
                post.setSelfText("");
            } else {
                String selfText = Utils.modifyMarkdown(Utils.trimTrailingWhitespace(data.getString(JSONUtils.SELFTEXT_KEY)));
                post.setSelfText(selfText);
                if (data.isNull(JSONUtils.SELFTEXT_HTML_KEY)) {
                    post.setSelfTextPlainTrimmed("");
                } else {
                    String selfTextPlain = Utils.trimTrailingWhitespace(
                            Html.fromHtml(data.getString(JSONUtils.SELFTEXT_HTML_KEY))).toString();
                    post.setSelfTextPlain(selfTextPlain);
                    if (selfTextPlain.length() > 250) {
                        selfTextPlain = selfTextPlain.substring(0, 250);
                    }
                    if (!selfText.equals("")) {
                        Pattern p = Pattern.compile(">!.+!<");
                        Matcher m = p.matcher(selfText.substring(0, Math.min(selfText.length(), 400)));
                        if (m.find()) {
                            post.setSelfTextPlainTrimmed("");
                        } else {
                            post.setSelfTextPlainTrimmed(selfTextPlain);
                        }
                    } else {
                        post.setSelfTextPlainTrimmed(selfTextPlain);
                    }
                }
            }
        }
        if (data.getBoolean("read")) {
            post.markAsRead();
        }
        if (!data.isNull("my_vote")) {
            post.setVoteType(data.getInt("my_vote"));
            if(post.getVoteType() == 1)
                post.setUpvotes(post.getUpvotes() - 1);
            else if (post.getVoteType() == -1)
                post.setDownvotes(post.getDownvotes() - 1);
        }
        if (!data.getJSONObject("post").isNull("body")) {
            String body = data.getJSONObject("post").getString("body");
            post.setSelfText(body);
            post.setSelfTextPlain(body);
            post.setSelfTextPlainTrimmed(body.trim());
        }
        if (data.getJSONObject("post").getBoolean("featured_community")) {
            post.setFeaturedInCommunity(true);
        }

        if (data.getJSONObject("post").getBoolean("featured_local")) {
            post.setFeaturedOnInstance(true);
        }


        return post;
    }

    private boolean isModerator(JSONArray moderators, String username) {
        for (int i = 0; i < moderators.length(); i++) {
            try {
                if (moderators.getJSONObject(i).getString("name").equals(username)) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public interface ParsePostsListingListener {
        void onParsePostsListingSuccess(LinkedHashSet<Post> newPostData, String lastItem);
        void onParsePostsListingFail();
    }

    public interface ParsePostListener {
        void onParsePostSuccess(Post post);

        void onParsePostFail();
    }

    public interface ParseRandomPostListener {
        void onParseRandomPostSuccess(String postId, String subredditName);

        void onParseRandomPostFailed();
    }

    public static int[] getImageDimension(String imageUrl) {


        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(imageUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.connect();
            InputStream input = urlConnection.getInputStream();

            //Just decode image size, not the image itself
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);

            return new int[]{options.outWidth, options.outHeight};
        } catch (IOException e) {
            Log.e("getImageDimension", "Error reading image dimensions", e);
            return new int[]{0, 0};
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    static class ImageInfo {
        private String caption;
        private String imageUrl;

        public ImageInfo(String caption, String imageUrl) {
            this.caption = caption;
            this.imageUrl = imageUrl;
        }

        public String getCaption() {
            return caption;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }


    public static List<ImageInfo> extractImages(String markdown) {
        List<ImageInfo> images = new ArrayList<>();

        // Regular expression to match markdown image syntax ![alt text](image URL)
        Pattern pattern = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");
        Matcher matcher = pattern.matcher(markdown);

        // Find all matches and extract image URLs and captions
        while (matcher.find()) {
            String caption = matcher.group(1);
            String imageUrl = matcher.group(2);
            images.add(new ImageInfo(caption, imageUrl));
        }

        return images;
    }
}
