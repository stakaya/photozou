import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.microedition.io.Connector;

import com.nttdocomo.io.HttpConnection;
import com.nttdocomo.ui.Dialog;
import com.nttdocomo.ui.IApplication;

/**
 * MediaCollection <BR>
 * ���̃A�v���P�[�V�����Ŏg�p���郁�f�B�A�f�[�^���Ǘ�����N���X�ł��B
 * @version 1.0
 */
public final class ScratchPad {

    /**
     * <code>bar</code> �v���O���X�o�[
     */
    private static Progress bar = new Progress(false);

    /**
     * <code>COMMENT</code> �R�����g
     */
    private static final int COMMENT = 500;

    /**
     * <code>HEAD</code> �w�b�_�T�C�Y
     */
    private static final int HEAD = 2;

    /**
     * <code>MAX</code> �ő�l
     */
    public static final int MAX = 200000;

    /**
     * <code>mediaLength</code> �X�N���b�`�p�b�h�̕ۑ��T�C�Y
     */
    private static int mediaLength = 0;

    /**
     * <code>mediaIndex</code> �X�N���b�`�p�b�h�̃I�t�Z�b�g
     */
    private static int mediaIndex = 1;

    /**
     * <code>URL</code> �g�p���\�[�X�ꗗ
     */
    private static final String[] URL = {
		"image/bg_main.gif",
		"image/bg_percent.gif",
		"image/bg.gif",
		"image/bg_start.gif",
		"image/icon_left_arrow.gif",
		"image/icon_arrow.gif"
    };

    /**
     * <code>resource</code> ���\�[�X�f�[�^
     */
    private static Vector resource = new Vector();

    /**
     * <code>mediaData</code> ���f�B�A�f�[�^
     */
    private static Vector mediaData = new Vector();

    /**
     * <code>SP</code> �X�N���b�`�p�b�h���ʎq
     */
    private static final String SP = "scratchpad:///0";

    /**
     * <code>TYPE_SOUND</code> �������ʎq
     * <code>TYPE_MOVIE</code> ���掯�ʎq
     * <code>TYPE_PICTURE</code> �Î~�掯�ʎq
     */
    public static final String
        TYPE_SOUND   = "WAV",
        TYPE_MOVIE   = "MOV",
        TYPE_PICTURE = "PIC";

    /**
     * �C���[�W�I�u�W�F�N�g��ۑ����܂��B
     * @param in  �摜�f�[�^�ǂݍ��݃I�u�W�F�N�g
     * @param type �I�u�W�F�N�g�^�C�v
     * @param comment �R�����g
     * @return ��������
     */
    public static boolean addImage(final InputStream in, final String type, final String comment) {
    	MediaData md = new MediaData(in, type, comment);
    	mediaLength += md.length();
    	mediaData.addElement(md);

    	if (mediaLength > MAX) {
    		return false;
    	}

		return save();
    }

    /**
     * �C���[�W�I�u�W�F�N�g��ۑ����܂��B
     * @param md  �摜�f�[�^
     * @return ��������
     */
    public static boolean addImage(final MediaData md) {
    	mediaLength += md.length();
    	mediaData.addElement(md);

    	if (mediaLength > MAX) {
    		return false;
    	}

		return save();
    }

    /**
     * �X�N���b�`�p�b�h���烁�f�B�A�f�[�^��ǂݍ��݁AMediaData���쐬���܂��B
     * @param offset �I�t�Z�b�g
     * @param textSize �e�L�X�g�̃T�C�Y
     * @param dataSize ���f�B�A�f�[�^�̃T�C�Y
     * @param type �^�C�v
     * @return MediaData �ԋp����郁�f�B�A�f�[�^��use()����Ă��܂��B
     */
    private static MediaData createMediaData(
    	final int offset, final int textSize, final int dataSize, final String type) {
        MediaData md = null;

        try {
        	md = new MediaData(Connector.openInputStream(SP + ";pos=" + offset), textSize, dataSize, type);
        } catch (Exception e) {
            Dialog dialog = new Dialog(Dialog.DIALOG_INFO, e.getClass().getName());
            dialog.setText("���f�B�A�f�[�^�̍쐬�Ɏ��s���܂����B\n" + e.getMessage());
            dialog.show();
        }
        return md;
    }

    /**
     * ���f�B�A�f�[�^�������ۑ�����Ă��邩�ԋp����B <br>
     * �N���b�`�p�b�h�̂P�o�C�g�ڂ�ǂݍ��݁A�ݒ肳��Ă���ꍇ�� �����ԋp���܂��B
     * @return ���f�B�A����ԋp
     */
    private static int getCount() {
        InputStream in = null;
        int count = 0;

        try {
            in = Connector.openDataInputStream(SP);
            count = in.read();
        } catch (Exception e) {
            Dialog dialog = new Dialog(Dialog.DIALOG_INFO, "��Q���");
            dialog.setText("�X�N���b�`�p�b�h�̓ǂݍ��݂Ɏ��s���܂����B");
            dialog.show();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (Exception e) {
	        	e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * �C���[�W�I�u�W�F�N�g���擾���܂��B
     * @return �C���[�W�I�u�W�F�N�g
     */
    public static MediaData getMediaData() {
        return (MediaData) mediaData.firstElement();
    }

    /**
     * �C���[�W�I�u�W�F�N�g���擾���܂��B
     * @param index �C���[�W�C���f�b�N�X
     * @return �C���[�W�I�u�W�F�N�g
     */
    public static MediaData getMediaData(final int index) {
        if (index > mediaData.size()) {
            return null;
        }
        return (MediaData) mediaData.elementAt(index);
    }

    /**
     * �C���[�W�I�u�W�F�N�g���擾���܂��B
     * @param index �C���[�W�C���f�b�N�X
     * @return �C���[�W�I�u�W�F�N�g
     */
    public static MediaData getResource(final int index) {
        if (index >= resource.size()) {
            return null;
        }
        return (MediaData) resource.elementAt(index);
    }

    /**
     * �X�N���b�`�p�b�h����C���[�W�f�[�^�̃T�C�Y��ǂݍ��݂܂��B
     * @param offset �I�t�Z�b�g
     * @return �C���[�W�̃T�C�Y
     */
    private static int getMediaSize(final int offset) {
        InputStream in = null;
        int length = 0;

        try {
            in = Connector.openInputStream(SP + ";pos=" + offset);
            length |= (in.read() << 8);
            length |= in.read();
        } catch (Exception e) {
            Dialog dialog = new Dialog(Dialog.DIALOG_INFO, e.getClass().getName());
            dialog.setText("���f�B�A�T�C�Y�̎擾�Ɏ��s���܂����B\n" + e.getMessage());
            dialog.show();
            length = 0;
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (Exception e) {
	        	e.printStackTrace();
            }
        }
        return length;
    }

    /**
     * �X�N���b�`�p�b�h����C���[�W�f�[�^�̃T�C�Y��ǂݍ��݂܂��B
     * @param offset �I�t�Z�b�g
     * @return �C���[�W�̃T�C�Y
     */
    private static int getMediaType(final int offset) {
        InputStream in = null;
        int length = 0;

        try {
            in = Connector.openInputStream(SP + ";pos=" + offset);
            length = in.read();
        } catch (Exception e) {
            Dialog dialog = new Dialog(Dialog.DIALOG_INFO, e.getClass().getName());
            dialog.setText("���f�B�A�T�C�Y�̎擾�Ɏ��s���܂����B\n" + e.getMessage());
            dialog.show();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (Exception e) {
	        	e.printStackTrace();
            }
        }
        return length;
    }

    /**
     * �T�[�o���� URL �Ŏw�肳�ꂽ�t�@�C�����_�E�����[�h���܂��B
     * @param file �_�E�����[�h����t�@�C���̃t�@�C��
     * @return �_�E�����[�h���ꂽ�f�[�^
     *         <br>����Ƀ_�E�����[�h�ł��Ȃ��ꍇ�Anull���ԋp����܂��B
     */
    private static MediaData getServerResource(final String file) {
        HttpConnection http = null;
        MediaData md = null;

        // �f�[�^�̃_�E�����[�h
        try {
            int contentLength = 0;
            String url = IApplication.getCurrentApp().getSourceURL() + file;
            String type = file.substring(file.length() - 3);
            http = (HttpConnection) Connector.open(url, Connector.READ);
            http.setRequestMethod(HttpConnection.GET);
            http.connect();
        	md = new MediaData(http.openInputStream(), file, type);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (null != http) {
                    http.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return md;
    }

    /**
     * �I�u�W�F�N�g�̐���Ԃ��܂��B
     * @return �I�u�W�F�N�g��
     */
    public static int length() {
    	return mediaData.size();
    }

    /**
     * �X�N���b�`�p�b�h����C���[�W�����[�h���܂��B
     * @return �ǂݍ��݂����������ꍇ�Atrue <br>
     *          �ǂݍ��݂Ɏ��s�����ꍇ�Afalse
     */
    public static boolean load() {
        int length = getCount();
        int textSize = 0;
        int dataSize = 0;
        int offset = 1;
        String type = null;

        // ���\�[�X�̏������ł��ĂȂ��ꍇ
        if (length == 0) {
            bar.reset(URL.length);
            bar.setMessage("�_�E�����[�h���Ă��܂��B");
            bar.show();

            for (int i = 0; i < URL.length; i++) {
                // �t�@�C���̃_�E�����[�h
            	MediaData md = getServerResource(URL[i]);
                if (null == md) {
                	System.out.println("�_�E�����[�h�t�@�C���ُ�");
                    return false;
                }

            	byte[] data = md.toByteArray();
            	byte[] text = md.getComment();

                // �e�ʃI�[�o�[�̏ꍇ
            	if (mediaIndex + 1 + HEAD + text.length + HEAD + data.length > MAX) {
                    return false;
            	}

            	// �X�N���b�`�p�b�h�ɕۑ�
                if (!save(text, data, mediaIndex, md.getType())) {
                	System.out.println("�X�N���b�`�p�b�h�ۑ����s");
                    return false;
                }

                // ���\�[�X�ۑ�
                resource.addElement(md);

                // �X�N���b�`�p�b�h�ւ̏������݈ʒu��
                // ��� + �w�b�_ + �R�����g + �w�b�_ + �f�[�^���V�t�g����B
                mediaIndex += 1 + HEAD + text.length + HEAD + data.length;
                bar.add();
            }

            // �X�N���b�`�p�b�h�̂P�o�C�g�ڂ̃T�C�Y���X�V����
            if (!setSize(URL.length)) {
            	System.out.println("�X�N���b�`�p�b�h�T�C�Y�ۑ����s");
                return false;
            }
        }

        // �v���O���X�o�[�̏���
        bar.reset(length);
        bar.setMessage("�f�[�^��ǂݍ���ł��܂��B");
        bar.show();

        for (int i = 0; i < length; i++) {

        	// �^�C�v���擾
        	if (getMediaType(offset++) == 0) {
        		type = TYPE_PICTURE;
        	} else {
        		type = TYPE_MOVIE;
        	}

            // �X�N���b�`�p�b�h����e�L�X�g�T�C�Y���擾
        	textSize = getMediaSize(offset);
            if (0 > textSize) {
            	System.out.println("�X�N���b�`�p�b�h�ǂݏo�����s");
                return false;
            }

            // �X�N���b�`�p�b�h����C���[�W�T�C�Y���擾
            dataSize = getMediaSize(offset + HEAD + textSize);
            if (0 > dataSize) {
            	System.out.println("�X�N���b�`�p�b�h�ǂݏo�����s");
                return false;
            }

            // �X�N���b�`�p�b�h���烁�f�B�A�C���[�W���쐬
            MediaData md = createMediaData(HEAD + offset, textSize, dataSize, type);

            // �X�N���b�`�p�b�h����̓ǂݍ��݈ʒu���f�[�^�̒������V�t�g
            offset += HEAD + textSize + HEAD + dataSize;

            // ���\�[�X����
            if (i < URL.length) {
            	mediaIndex = offset;
                resource.addElement(md);
            } else {
                mediaLength += md.length();
                mediaData.addElement(md);
            }

            // �o�[�i�s
            bar.add();
        }
        return true;
    }

    /**
     * �C���[�W�I�u�W�F�N�g��j�����܂��B
     * @param index �C���f�b�N�X
     * @return ���f�B�A�f�[�^�̕ۑ������������ꍇ�Atrue <br>
     *          ���f�B�A�f�[�^�̕ۑ��Ɏ��s�����ꍇ�Afalse
     */
    public static boolean remove(final int index) {
        if (index > mediaData.size()) {
            return false;
        }

    	MediaData md = (MediaData) mediaData.elementAt(index);
    	mediaLength -= md.length();
    	mediaData.removeElementAt(index);
		return save();
    }

    /**
     * �C���[�W�I�u�W�F�N�g��j�����܂��B
     * @param index �C���f�b�N�X
     * @return ���f�B�A�f�[�^�̕ۑ������������ꍇ�Atrue <br>
     *          ���f�B�A�f�[�^�̕ۑ��Ɏ��s�����ꍇ�Afalse
     */
    public static boolean remove(final Object index) {
    	MediaData md = (MediaData) index;
    	mediaLength -= md.length();
    	mediaData.removeElement(index);
		return save();
    }

    /**
     * �C���[�W�I�u�W�F�N�g��j�����܂��B
     * @return ���f�B�A�f�[�^�̕ۑ������������ꍇ�Atrue <br>
     *          ���f�B�A�f�[�^�̕ۑ��Ɏ��s�����ꍇ�Afalse
     */
    public static boolean removeAll() {
    	mediaLength = 0;
    	mediaData.removeAllElements();
		return save();
    }

    /**
     * ���f�B�A�f�[�^���X�N���b�`�p�b�h�ɕۑ����܂��B
     * @return ���f�B�A�f�[�^�̕ۑ������������ꍇ�Atrue <br>
     *          ���f�B�A�f�[�^�̕ۑ��Ɏ��s�����ꍇ�Afalse
     */
    public static boolean save() {
    	int index = mediaIndex;

        for (int i = 0; i < mediaData.size(); i++) {
        	MediaData md = (MediaData) mediaData.elementAt(i);
        	byte[] data = md.toByteArray();
        	byte[] text = md.getComment();

            // �e�ʃI�[�o�[�̏ꍇ
        	if (index + 1 + HEAD + text.length + HEAD + data.length > MAX) {
                return false;
        	}

        	// �X�N���b�`�p�b�h�ɕۑ�
            if (!save(text, data, index, md.getType())) {
                // �X�N���b�`�p�b�h�ւ̕ۑ����s
                return false;
            }

            // �X�N���b�`�p�b�h�̂P�o�C�g�ڂ̃T�C�Y���X�V����
            if (!setSize(i + 1 + URL.length)) {
                // �X�N���b�`�p�b�h�ւ̕ۑ����s
                return false;
            }

            // �X�N���b�`�p�b�h�ւ̏������݈ʒu��
            // ��� + �w�b�_ + �R�����g + �w�b�_ + �f�[�^���V�t�g����B
            index += 1 + HEAD + text.length + HEAD + data.length;
        }

        return setSize(mediaData.size() + URL.length);
    }

    /**
     * �f�[�^���X�N���b�`�p�b�h�ɕۑ����܂��B
     * @param text �ۑ�����R�����g
     * @param data �ۑ�����f�[�^
     * @param offset �I�t�Z�b�g
     * @param type �R���e���c�^�C�v
     * @return �ۑ������������ꍇ�Atrue <br>
     *          �ۑ��Ɏ��s�����ꍇ�Afalse
     */
    private static boolean save(final byte[] text, final byte[] data, final int offset, final String type) {
        OutputStream out = null;
        int length = 0;

        try {
            out = Connector.openOutputStream(SP + ";pos=" + offset);

            // ����/�Î~�攻��
            if (type.equals(TYPE_PICTURE)) {
                out.write(0 & 0xff);
            } else {
                out.write(1 & 0xff);
            }

            // �����O�X
            length = text.length;
            out.write((length >>> 8) & 0xff);
            out.write(length & 0xff);

            // �R�����g��������
            out.write(text);

            // �����O�X
            length = data.length;
            out.write((length >>> 8) & 0xff);
            out.write(length & 0xff);

            // �f�[�^��������
            out.write(data);
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (Exception e) {
	        	e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * �X�N���b�`�p�b�h��Ƀ��f�B�A�f�[�^������ێ����܂��B
     * @param size �T�C�Y
     * @return ��������
     */
    private static boolean setSize(final int size) {
        OutputStream out = null;
        if (size > 255) {
            return false;
        }

        try {
            out = Connector.openOutputStream(SP);
            out.write(size & 0xff);
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * �R���X�g���N�^
     */
    private ScratchPad() {
    }
}