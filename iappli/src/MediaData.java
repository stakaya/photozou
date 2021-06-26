import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * �C���[�W�f�[�^�i�[�p�N���X
 */
public final class MediaData {

	/**
	 * <code>comment</code> �R�����g
	 */
	private String comment;

	/**
	 * <code>data</code> �C���[�W�f�[�^
	 */
	private byte[] data;

	/**
	 * <code>type</code> �^�C�v
	 */
	private String type;

	/**
	 * �R���X�g���N�^
	 * @param in �C���[�W�f�[�^���̓X�g���[��
     * @param textSize �e�L�X�g�̃T�C�Y
     * @param dataSize ���f�B�A�f�[�^�̃T�C�Y
	 * @param tp �^�C�v
	 */
	public MediaData(final InputStream in, final int textSize, final int dataSize, final String tp) {
        try {
			this.type = tp;
			byte[] temp = new byte[textSize];
			this.data = new byte[dataSize];

			// �R�����g�ǂݍ���
			in.read(temp);
			this.comment = new String(temp);

			// �w�b�_�ǂݔ�΂�
			in.read();
			in.read();

			// �f�[�^�ǂݍ���
			in.read(this.data);
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (Exception e) {
	        	e.printStackTrace();
            }
        }
	}

	/**
	 * �R���X�g���N�^
	 * @param in �C���[�W�f�[�^���̓X�g���[��
	 * @param tp �^�C�v
	 * @param text �R�����g
	 */
	public MediaData(final InputStream in, final String tp, final String text) {
        try {
			this.type = tp;
			this.data = new byte[in.available()];
			this.comment = text;
			in.read(this.data);
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (Exception e) {
	        	e.printStackTrace();
            }
        }
	}

	/**
	 * �R���X�g���N�^
	 * @param text �R�����g
	 */
	public MediaData(final String text) {
		this.type = "";
		this.data = new byte[0];
		this.comment = text;
	}

	/**
	 * �R�����g�f�[�^��ԋp
	 * @return �R�����g�f�[�^
	 */
	public byte[] getComment() {
		return this.comment.getBytes();
	}

	/**
	 * �����O�X��ԋp
	 * @return �����O�X
	 */
	public int length() {
		return this.data.length;
	}

	/**
	 * �R�����g��ԋp
	 * @return �R�����g
	 */
	public String getText() {
		return this.comment;
	}

	/**
	 * �C���[�W�f�[�^��ԋp
	 * @return �C���[�W�f�[�^
	 */
	public byte[] toByteArray() {
		return this.data;
	}

	/**
	 * �C���[�W�f�[�^��ԋp
	 * @return �C���[�W�f�[�^
	 */
	public InputStream toInputStream() {
		return new ByteArrayInputStream(this.data);
	}

	/**
	 * �^�C�v��ԋp
	 * @return �^�C�v
	 */
	public String getType() {
		return this.type;
	}
}
