import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Calendar;

import javax.microedition.io.Connector;

import com.nttdocomo.io.HttpConnection;
import com.nttdocomo.ui.Dialog;
import com.nttdocomo.util.Phone;

/**
 * Communication<BR>
 * ���̃A�v���P�[�V�����Ŏg�p����ʐM���`����N���X�ł��B
 * <p>
 * @version 1.0
 * @author takaya
 * </p>
 */
public final class Communication {

    /**
     * <code>bar</code> �v���O���X�o�[
     */
    private static Progress bar = new Progress(true);

    /**
     * <code>uniq</code> ���s�ςݓ��t
     */
    private static String uniq = "";

    /**
     * �ʐM�T�C�Y�ƒʐM�w�b�_�T�C�Y
     * DoJa2.0��5K�o�C�g  mova 504i�y��504iS�V���[�Y
     * DoJa3.0��10K�o�C�g mova 505i�y��505iS�A506i�V���[�Y
     * DoJa3.5��80K�o�C�g FOMA 900i�V���[�Y
     * <code>DATE_SIZE    </code> ���t�T�C�Y
     * <code>SIM_ID_SIZE  </code> SIMID�T�C�Y
     * <code>TERM_ID_SIZE </code> �����ԍ��T�C�Y
     * <code>SEQ_NO_SIZE  </code> �V�[�P���XNO�T�C�Y
     * <code>TYPE_SIZE    </code> ���ʎq�T�C�Y
     * <code>LENGTH_SIZE  </code> �f�[�^�����O�X�T�C�Y
     * <code>BAR_SIZE     </code> �v���O���X�o�[�̈ʒu���킹�p
     * <code>MAIL_SIZE    </code> ���[���T�C�Y
     * <code>ALBUM_SIZE   </code> �A���o���T�C�Y
     * <code>PASSWORD_SIZE</code> �p�X���[�h�T�C�Y
     * <code>MAX_SEND_SIZE</code> ���M�o�b�t�@�T�C�Y
     */
    public static final int
        DATE_SIZE     =   14,
        SIM_ID_SIZE   =   20,
        TERM_ID_SIZE  =   15,
        SEQ_NO_SIZE   =    5,
        TYPE_SIZE     =    5,
        MAIL_SIZE     =  256,
        PASSWORD_SIZE =   20,
        ALBUM_SIZE    =   20,
        LENGTH_SIZE   =    6,
        BAR_SIZE      =    2,
        MAX_SEND_SIZE = 80000
                      - DATE_SIZE
                      - SIM_ID_SIZE
                      - TERM_ID_SIZE
                      - SEQ_NO_SIZE
                      - TYPE_SIZE
                      - LENGTH_SIZE
                      - MAIL_SIZE
                      - PASSWORD_SIZE
                      - ALBUM_SIZE;

    /**
     * �g�ѓd�b��ID���
     * <code>SIM_ID </code> SIM ID
     * <code>TERM_ID</code> TERM ID
     */
    public static final String
        SIM_ID  = Phone.getProperty(Phone.USER_ID),
        TERM_ID = Phone.getProperty(Phone.TERMINAL_ID);

    /**
     * ���ݓ��t�Ǝ��Ԃ�Ԃ�
     * @return ���ݓ��t(YYYYMMDDHHMMSS)
     */
    public static String getSystemDateTime() {
        Calendar calendar = Calendar.getInstance();
        String temp = Long.toString(calendar.get(Calendar.YEAR)  * 10000000000L
                           + (calendar.get(Calendar.MONTH) + 1)  * 100000000
                           + calendar.get(Calendar.DAY_OF_MONTH) * 1000000
                           + calendar.get(Calendar.HOUR_OF_DAY)  * 10000
                           + calendar.get(Calendar.MINUTE)       * 100
                           + calendar.get(Calendar.SECOND));

        // �������t�̏ꍇ�͍Ď擾����
        if (temp.equals(uniq)) {
            temp = getSystemDateTime();
        }

        return uniq = temp;
    }

    /**
     * �ʐM�p�p�b�f�B���O.
     * �w�背���O�X�ɕ���������H����B
     * �����O�X�̌����X�y�[�X�Ŗ��߂�B
     * @param data �f�[�^
     * @param length �w�背���O�X
     * @return ��������
     */
    public static String padding(final String data, final int length) {

        // null�̏ꍇ
        if (data == null) {
            return null;
        }

        // �w��T�C�Y���傫���ꍇ
        if (data.length() > length) {
            return data.substring(length);
        } else if (data.length() == length) {
            return data;
        }

        // ���ɃX�y�[�X�𖄂߂�
        String temp = data;
        for (int i = data.length(); i < length; i++) {
            temp += " ";
        }

        return temp;
    }

    /**
     * �ʐM�p��������.
     * �w��f���~�^�ŕ�����𕪊�����B
     * @param data �f�[�^
     * @param length �w�背���O�X
     * @return ��������
     */
    public static String[] split(final String data, final String delimiter) {

        // null�̏ꍇ
        if (data == null || delimiter == null) {
            return null;
        }

        int length = 0;
        String temp = data;
        while (temp.indexOf(delimiter) > 0) {
            length++;
            temp = temp.substring(temp.indexOf(delimiter) + 1);
        }

        int i = 0;
        temp = data;
        String[] result = new String[length];
        while (temp.indexOf(delimiter) > 0) {
            result[i++] = temp.substring(0, temp.indexOf(delimiter));
            temp = temp.substring(temp.indexOf(delimiter) + 1);
        }

        return result;
    }

    /**
     * �f�[�^���M
     * @param url URL
     * @param data ���M�f�[�^
     * @return ��������
     */
    public static synchronized String send(final String url, final byte[] data) {

        HttpConnection    conn = null;
        OutputStream      out  = null;
        InputStream       in   = null;
        InputStreamReader rec  = null;
        StringBuffer      temp = new StringBuffer();
        Dialog dialog = new Dialog(Dialog.DIALOG_ERROR, "�o�^�G���[");

        try {
            // HTTP�ڑ��̏���
            conn = (HttpConnection) Connector.open(url, Connector.READ_WRITE, true);
            conn.setRequestMethod(HttpConnection.POST);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // �f�[�^�𑗐M
            out = conn.openOutputStream();
            out.write(data);

        } catch (Exception e) {
            dialog.setText("HTTP�ڑ���O����\n");
            dialog.show();
            return null;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                dialog.setText("HTTP�f�[�^���M���s\n");
                dialog.show();
                return null;
            }
        }

        try {
            // �T�[�o�ڑ�
            conn.connect();
            if (conn.getResponseCode() != HttpConnection.HTTP_OK) {
                return null;
            }

            // �f�[�^��M
            in = conn.openInputStream();
            rec = new InputStreamReader(in);
            int buffer;
            while ((buffer = rec.read()) != -1) {
                temp.append((char) buffer);
            }

            // ��M���ʔ���
            if (!temp.toString().startsWith("OK")) {
                System.out.println("�T�[�o�G���[���� Cord:" + temp.toString());
                System.out.println(temp.toString());
                return null;
            }
        } catch (Exception e) {
            dialog.setText("�ʐM�G���[�������������܂���\n");
            dialog.show();
            return null;
        } finally {
            try {
                // �R�l�N�V�����ؒf
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (rec != null) {
                    rec.close();
                }
            } catch (Exception e) {
                dialog.setText("�ʐM�ؒf�Ɏ��s���܂���\n");
                dialog.show();
                return null;
            }
        }
        return temp.toString();
    }

    /**
     * �f�[�^���M����
     * @param url URL
     * @param type ���ʎq
     * @param comment �R�����g
     * @param data ���M�f�[�^
     * @return ��������
     */
    public static String authorize(final String url, final String mail, final String password) {

        byte[] packet = new byte[MAX_SEND_SIZE];
        String header = null;
        String result = null;
        String date = getSystemDateTime();
        ByteArrayOutputStream out = null;
        int length = 0;
        int i = 0;

        try {
            // �v���O���X�o�[�̏���
            bar.reset(BAR_SIZE);
            bar.setMessage("�F�ؒ��ł��B");
            bar.show();

            // �R�����g�f�[�^���M
            header = padding(mail, MAIL_SIZE)
                   + padding(password, PASSWORD_SIZE);

            // �f�[�^�ҏW
            byte[] sendHeader = header.getBytes();
            out = new ByteArrayOutputStream(sendHeader.length);
            out.write(sendHeader);
            out.close();
            bar.add();

            // �f�[�^���M
            result = send(url, out.toByteArray());
            if (null == result) {
                System.out.println("�f�[�^���M���s");
                return null;
            }
        } catch (Exception e) {
            System.out.println("�f�[�^�ҏW�ŗ�O����");
            return null;
        }

        return result;
    }

    /**
     * �f�[�^���M����
     * @param url URL
     * @param type ���ʎq
     * @param mail ���[��
     * @param pass �p�X���[�h
     * @param album �A���o��
     * @param titel �^�C�g��
     * @param data ���M�f�[�^
     * @return ��������
     */
    public static boolean
        sendData(final String url, final String type, final String mail,
                 final String pass,final String album, final String title, final InputStream data) {

        byte[] packet = new byte[MAX_SEND_SIZE];
        String header = null;
        String date = getSystemDateTime();
        ByteArrayOutputStream out = null;
        int length = 0;
        int i = 0;

        try {

            // �f�[�^�������ꍇ
            if (data == null) {
                return false;
            }

            // �v���O���X�o�[�̏���
            bar.reset(data.available() / MAX_SEND_SIZE + BAR_SIZE);
            bar.setMessage("�f�[�^�𑗐M���Ă��܂��B");
            bar.show();

            // camera����̃f�[�^�𑗐M
            while ((length = data.read(packet)) != -1) {
                header = padding(date,                     DATE_SIZE)
                       + padding(SIM_ID,                   SIM_ID_SIZE)
                       + padding(TERM_ID,                  TERM_ID_SIZE)
                       + padding(Integer.toString(++i),    SEQ_NO_SIZE)
                       + padding(type,                     TYPE_SIZE)
                       + padding(mail,                     MAIL_SIZE)
                       + padding(pass,                     PASSWORD_SIZE)
                       + padding(album,                    ALBUM_SIZE)
                       + padding(Integer.toString(length), LENGTH_SIZE);
                byte[] sendHeader = header.getBytes();
                out = new ByteArrayOutputStream(sendHeader.length + length);
                out.write(sendHeader);
                out.write(packet);
                out.close();
                bar.add();

                // �f�[�^���M
                if (null == send(url, out.toByteArray())) {
                    System.out.println("�f�[�^���M���s");
                    return false;
                }
            }

            packet = title.getBytes();
            length = title.getBytes().length;

            // �I���f�[�^���M
            header = padding(date,    DATE_SIZE)
                   + padding(SIM_ID,  SIM_ID_SIZE)
                   + padding(TERM_ID, TERM_ID_SIZE)
                   + padding("END",   SEQ_NO_SIZE)
                   + padding(type,    TYPE_SIZE)
                   + padding(mail,    MAIL_SIZE)
                   + padding(pass,    PASSWORD_SIZE)
                   + padding(album,   ALBUM_SIZE)
                   + padding(Integer.toString(length), LENGTH_SIZE);

            // �f�[�^�ҏW
            byte[] sendHeader = header.getBytes();
            out = new ByteArrayOutputStream(sendHeader.length + length);
            out.write(sendHeader);
            out.write(packet);
            out.close();
            bar.add();

            // �f�[�^���M
            if (null == send(url, out.toByteArray())) {
                System.out.println("�f�[�^���M���s");
                return false;
            }
        } catch (Exception e) {
            System.out.println("�f�[�^�ҏW�ŗ�O����");
            return false;
        }

        return true;
    }

    /**
     * �R���X�g���N�^.
     * <br>�A�v���P�[�V���������ڃC���X�^���X�𐶐����邱�Ƃ͂ł��܂���B
     */
    private Communication() {
    }
}
