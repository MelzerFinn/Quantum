package util;

import com.google.common.primitives.Bytes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Util {

    public static final byte[] delimiterA = new byte[]{0x0};
    public static final byte delimiter = 0x0;
    public static final byte ok = 0x1;
    public static final byte initConnection = 0x2;
    public static final byte restartConnection = 0x3;
    public static final byte wrongPassword = 0x4;
    public static final byte sendTo = 0x5;
    public static final byte endConnection = 0x6;
    public static final byte error = 0x7;
    public static final byte getMessages = 0x8;
    public static final byte getMessagesZip = 0x9;
    public static final byte noNewMessages = 0xA;

    public static byte[] addCommand(byte command, byte[] toAddTo) throws IOException {
        return Bytes.concat(new byte[]{command}, toAddTo);
    }

    /**
     * Unzips a byte array
     *
     * @param toUnzip the byte array to unzip
     * @return the unzipped byte array
     * @throws IOException an IOException if something does bot work
     */
    public static byte[] unzip(byte[] toUnzip) throws IOException {
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(toUnzip));
        byte[] res = zis.readAllBytes();
        zis.close();

        return res;
    }

    /**
     * Zips a byte array
     *
     * @param toZip the byte array to zip
     * @return the zipped byte array
     * @throws IOException an IOException if something does bot work
     */
    public static byte[] zip(byte[] toZip) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ZipOutputStream zout = new ZipOutputStream(bout);
        zout.write(toZip);
        zout.flush();

        byte[] res = bout.toByteArray();
        zout.close();
        bout.close();

        return res;
    }

    public static boolean contains(Byte b, byte[] bytes) {
        for (Byte b1 : bytes) {
            if (b.equals(b1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * splits a byte array by the delimiter 0x0
     *
     * @param input the array to split
     * @return the resulting byte arrays
     */
    public static byte[][] split(byte[] input) {
        ArrayList<byte[]> tmp = new ArrayList<>();
        int lastDelimiter = 0;
        for (int i = 0; i < input.length; i++) {
            if (((Byte) input[i]).equals(delimiter)) {
                tmp.add(Arrays.copyOfRange(input, lastDelimiter, i));
                lastDelimiter = i + 1;
            }
        }

        if (lastDelimiter != input.length) {
            tmp.add(Arrays.copyOfRange(input, lastDelimiter, input.length));
        }

        //Convert ArrayList to Array
        byte[][] res = new byte[tmp.size()][];
        for (int i = 0; i < tmp.size(); i++) {
            res[i] = tmp.get(i);
        }

        return res;
    }

    public static Result<Byte, byte[]> getCode(byte[] input) {
        byte code = input[0];
        byte[] tmp = Arrays.copyOfRange(input, 1, input.length);

        return new Result<Byte, byte[]>() {
            @Override
            public Byte getKey() {
                return code;
            }

            @Override
            public byte[] getValue() {
                return tmp;
            }
        };
    }

    public static byte[] generateChecksum(byte[] in) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(); //Should not be thrown
        }
        assert md != null;
        md.update(in);
        return md.digest();
    }
}