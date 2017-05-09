package android.os;

/**
 * Created by thom on 2017/2/12.
 */
public class UserHandle {

    /**
     * @hide A user id constant to indicate the "owner" user of the device
     */
    public static int USER_OWNER = 0;

    /**
     * @hide A user id constant to indicate the "system" user of the device
     */
    public static int USER_SYSTEM = 0;

    public static int myUserId() {
        throw new UnsupportedOperationException();
    }

    public static int getUserId(int uid) {
        throw new UnsupportedOperationException();
    }

}
