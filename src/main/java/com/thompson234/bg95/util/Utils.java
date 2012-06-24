package com.thompson234.bg95.util;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.util.Md5Utils;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;

public final class Utils {

    public static String urlEncode(String data) {

        try {
            return URLEncoder.encode(data, "UTF-8");
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }

    public static String md5HashString(String toHash) {

        try {
            final byte[] hash = Md5Utils.computeMD5Hash(toHash.getBytes());
            BigInteger bigInt = new BigInteger(1, hash);
            return Strings.padStart(bigInt.toString(16), 32, '0');
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }

    public static void threadSleep(long duration) {

        if (duration <= 0) {
            return;
        }

        try {
            Thread.sleep(duration);
        } catch (InterruptedException ex) {
            //noop
        }
    }

    public static ImmutableList<Object> selectAll(TagNode root, String xpath) {

        try {
            return ImmutableList.copyOf(root.evaluateXPath(xpath));
        } catch (XPatherException ex) {
            throw Throwables.propagate(ex);
        }
    }

    public static Object selectOne(TagNode root, String xpath) {

        final ImmutableList<Object> raw = selectAll(root, xpath);

        if (raw == null || raw.size() != 1) {
            throw new IllegalStateException("Could not find one result for '" + xpath + "'");
        }

        return raw.get(0);
    }

    public static Object selectFirst(TagNode root, String xpath) {

        final ImmutableList<Object> raw = selectAll(root, xpath);

        if (raw == null || raw.isEmpty()) {
            throw new IllegalStateException("Could not find any results for '" + xpath + "'");
        }

        return raw.get(0);
    }

    public static ImmutableList<TagNode> selectAllNodes(TagNode root, String xpath) {

        final ImmutableList<Object> rawNodes = selectAll(root, xpath);
        final ImmutableList.Builder<TagNode> builder = new ImmutableList.Builder<TagNode>();

        for (Object raw : rawNodes) {
            builder.add((TagNode) raw);
        }

        return builder.build();
    }

    public static TagNode selectOneNode(TagNode root, String xpath) {
        return (TagNode) selectOne(root, xpath);
    }

    public static TagNode selectFirstNode(TagNode root, String xpath) {
        return (TagNode) selectFirst(root, xpath);
    }

    public static String unescapeHtml(String html) {

        if (Strings.isNullOrEmpty(html)) {
            return html;
        }

        String toCaller = StringEscapeUtils.unescapeHtml4(html);
        return sanitizeSpaces(toCaller);
    }

    public static String sanitizeSpaces(String html) {
        return html.replace((char) 160, ' ');
    }

    public static String sanitizeTagNodeText(TagNode tagNode) {
        return Strings.emptyToNull(unescapeHtml(tagNode.getText().toString()).trim());
    }

    public static ImmutableList<String> selectAllStrings(TagNode root, String xpath) {
        return selectAllStrings(root, xpath, true);
    }

    public static ImmutableList<String> selectAllStrings(TagNode root, String xpath, boolean sanitize) {

        final ImmutableList<Object> rawNodes = selectAll(root, xpath);
        final ImmutableList.Builder<String> builder = new ImmutableList.Builder<String>();

        for (Object raw : rawNodes) {

            String value = raw.toString();

            if (sanitize) {
                value = unescapeHtml(value).trim();
            }

            builder.add(value);
        }

        return builder.build();
    }

    public static String selectOneString(TagNode root, String xpath) {
        return selectOneString(root, xpath, true);
    }

    public static String selectOneString(TagNode root, String xpath, boolean sanitize) {

        String value = selectOne(root, xpath).toString();

        if (sanitize) {
            value = unescapeHtml(value).trim();
        }

        return value;
    }

    public static String selectFirstString(TagNode root, String xpath) {
        return selectFirstString(root, xpath, true);
    }

    public static String selectFirstString(TagNode root, String xpath, boolean sanitize) {

        String value = selectFirst(root, xpath).toString();

        if (sanitize) {
            value = unescapeHtml(value).trim();
        }

        return value;
    }

    public static byte[] getS3Content(S3Object s3Object) {
        try {
            return ByteStreams.toByteArray(s3Object.getObjectContent());
        } catch (IOException ex) {
            throw Throwables.propagate(ex);
        }
    }

    public static String removePunctuation(String source) {

        String toCaller = StringUtils.remove(source, "`");
        toCaller = StringUtils.remove(toCaller, "'");
        toCaller = StringUtils.remove(toCaller, "\"");
        toCaller = StringUtils.remove(toCaller, ",");
        toCaller = StringUtils.remove(toCaller, ".");
        toCaller = StringUtils.remove(toCaller, "!");
        toCaller = StringUtils.remove(toCaller, ":");
        toCaller = StringUtils.remove(toCaller, ";");
        toCaller = StringUtils.remove(toCaller, "-");
        return StringUtils.stripAccents(toCaller);
    }

    public static List<ReplaceableAttribute> toReplaceableAttributes(final String name, Collection<String> values) {

        return toReplaceableAttributes(name, Lists.newArrayList(values));
    }

    public static List<ReplaceableAttribute> toReplaceableAttributes(final String name, List<String> values) {

        return Lists.transform(values, new Function<String, ReplaceableAttribute>() {
            boolean _replace = true;

            @Override
            public ReplaceableAttribute apply(@Nullable String value) {
                final ReplaceableAttribute ra = new ReplaceableAttribute(name, value, _replace);
                _replace = false;
                return ra;
            }
        });
    }
}
