import java.io.InputStream;

import com.nttdocomo.device.Camera;
import com.nttdocomo.system.ImageStore;
import com.nttdocomo.system.MovieStore;
import com.nttdocomo.ui.Dialog;
import com.nttdocomo.ui.Image;
import com.nttdocomo.ui.MediaImage;

/**
 * Camera<BR>
 * ���̃A�v���P�[�V�����Ŏg�p����J�������`����N���X�ł��B
 * <p>
 * @version 1.0
 * @author takaya
 * </p>
 */
public final class CameraDevice {

	/**
     * <code>camera</code> �J����
     */
    private static Camera camera = null;

	/**
     * <code>image</code> �I���摜
     */
    private static ImageStore image = null;

	/**
     * <code>movie</code> �I�𓮉�
     */
    private static MovieStore movie = null;

	/**
     * <code>type</code> �摜�t���O
     */
    private static int type = 0;

	/**
     * <code>dialog</code> �G���[�_�C�A���O
     */
    private static Dialog dialog = new Dialog(Dialog.DIALOG_ERROR, "�J������Q");

    /**
     * <code>PICTURE</code> �摜
     * <code>MOVIE  </code> ����
     * <code>PHOTO  </code> �摜�t�@�C��
     * <code>VIDEO  </code> ����t�@�C��
     */
    public static final int PICTURE = 0,
	                           MOVIE   = 1,
							   PHOTO   = 2,
	                           VIDEO   = 3;
    /**
     * <code>PIC_SIZE</code> �摜�T�C�Y
     * <code>MOV_SIZE</code> ����T�C�Y
     */
    public static final int[][] PIC_SIZE, MOV_SIZE;

    /**
     * <code>pictuerSize </code> �摜�T�C�Y��
     * <code>movieSize   </code> ����T�C�Y��
     */
    private static int pictuerSize = 0,
                         movieSize   = 0;

    // �J�����I�u�W�F�N�g�̏����ݒ�
    static {

        // �J�����I�u�W�F�N�g�̎擾
        camera = Camera.getCamera(0);

        // �A�ˋ@�\
        if (camera.isAvailable(Camera.DEV_CONTINUOUS_SHOT)) {
            // �A�ʋ@�\��}�~
            camera.setAttribute(Camera.DEV_CONTINUOUS_SHOT, Camera.ATTR_CONTINUOUS_SHOT_OFF);
        }

        // ���掿�ɐݒ�\�Ȃ�
        if (camera.isAvailable(Camera.DEV_QUALITY)) {
            // ���掿�ݒ�
            camera.setAttribute(Camera.DEV_QUALITY, Camera.ATTR_QUALITY_HIGH);
        }

        // �t���[���ݒ�@�\����̎�
        if (camera.isAvailable(Camera.DEV_FRAME_SHOT)) {
            // �t���[���B�e�s��
            camera.setAttribute(Camera.DEV_FRAME_SHOT, Camera.ATTR_FRAME_OFF);
        }

        // �T�E���h�ݒ�
        if (camera.isAvailable(Camera.DEV_SOUND)) {
            camera.setAttribute(Camera.DEV_SOUND, Camera.ATTR_VOLUME_MAX);
        }

        // �J�����T�C�Y���擾���ă\�[�g�e�[�u���֊i�[
        int [][] temp = camera.getAvailablePictureSizes();
        PIC_SIZE = sort(temp);
        temp = camera.getAvailableMovieSizes();
        MOV_SIZE = sort(temp);
    }

    /**
     * �J�����T�C�Y���\�[�g����
     * @param size �J�����T�C�Y
     * @return �\�[�g����
     */
    private static int[][] sort(final int[][] size) {
    	int[][] result = new int[size.length][2];
    	for (int i = 0, max = 0; i < size.length; i++) {
    	    for (int j = 0; j < size.length; j++) {
                if (result[i][0] * result[i][1] < size[j][0] * size[j][1]) {
                	result[i][0] = size[j][0];
                	result[i][1] = size[j][1];
                	max = j;
                }
    	    }
        	size[max][0] = 0;
        	size[max][1] = 0;
    	}
		return result;
    }

    /**
     * �C���[�W���폜
     */
    public static void dispose() {

    	// �I���摜������
        image = null;
        movie = null;

        // �B�e���ꂽ�ꍇ
        if (camera.getNumberOfImages() > 0) {
            camera.disposeImages();
        }
    }

    /**
     * �J�����ɂ���f�[�^�����o���ĕԋp����
     * @return �J��������擾�����f�[�^
     */
    public static InputStream getCameraData() {
        try {
            MediaImage mediaImage = null;

            // �ۑ����Ă���摜�̏ꍇ
        	if (type == PHOTO) {
	            // �B�e����Ȃ��ꍇ
	            if (image == null) {
	                return null;
	            }

	            // �[���ɕێ�����Ă���摜���擾
	            return image.getInputStream();
        	} else if (type == VIDEO) {
                // �B�e����Ȃ��ꍇ
                if (movie == null) {
                    return null;
                }

                // �[���ɕێ�����Ă��铮����擾
                return movie.getInputStream();
    	    }

	        // �B�e����Ȃ��ꍇ
	        if (camera.getNumberOfImages() == 0) {
	            return null;
	        }

            // �J�����ɕێ�����Ă���摜���擾
            return camera.getInputStream(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * �J�����ɂ���f�[�^�����o���ĕԋp����
     * @return �J��������擾�����f�[�^
     */
    public static Image getCameraImage() {
        try {
            MediaImage mediaImage = null;

            // �ۑ����Ă���摜�̏ꍇ
        	if (type == PHOTO) {
	            // �B�e����Ȃ��ꍇ
	            if (image == null) {
	                return null;
	            }

	            // �[���ɕێ�����Ă���摜���擾
	            mediaImage = image.getImage();
        	} else if (type == VIDEO) {
	            // �B�e����Ȃ��ꍇ
	            if (movie == null) {
	                return null;
	            }

	            // �[���ɕێ�����Ă��铮����擾
	            mediaImage = movie.getImage();
        	} else {
	            // �B�e����Ȃ��ꍇ
	            if (camera.getNumberOfImages() == 0) {
	                return null;
	            }

	            // �J�����ɕێ�����Ă���摜���擾
	            mediaImage = camera.getImage(0);
        	}

            mediaImage.use();
            return mediaImage.getImage();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * �J�����ɂ���f�[�^�����o���ĕԋp����
     * @return �J��������擾�����f�[�^
     */
    public static MediaImage getMediaImage() {
        try {
            MediaImage mediaImage = null;

            // �ۑ����Ă���摜�̏ꍇ
        	if (type == PHOTO) {
	            // �B�e����Ȃ��ꍇ
	            if (image == null) {
	                return null;
	            }

	            // �[���ɕێ�����Ă��铮����擾
	            mediaImage = image.getImage();
        	} else if (type == VIDEO) {
	            // �B�e����Ȃ��ꍇ
	            if (movie == null) {
	                return null;
	            }

	            // �[���ɕێ�����Ă��铮����擾
	            mediaImage = movie.getImage();
        	} else {
	            // �B�e����Ȃ��ꍇ
	            if (camera.getNumberOfImages() == 0) {
	                return null;
	            }

	            // �J�����ɕێ�����Ă���摜���擾
	            mediaImage = camera.getImage(0);
        	}

            mediaImage.use();
            return mediaImage;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * �C���[�W���𒲂ׂ�
     * @return �摜�̐�
     */
    public static int getNumberOfImages() {

    	// �ۑ����Ă���摜�̏ꍇ
    	if (type == PHOTO) {
            // �B�e����Ȃ��ꍇ
            if (image == null) {
                return 0;
            }

            return 1;
    	} else if (type == VIDEO) {
            // �B�e����Ȃ��ꍇ
            if (movie == null) {
                return 0;
            }

            return 1;
    	}
        return camera.getNumberOfImages();
    }

    /**
     * ������B���ă��f�B�A�C���[�W���i�[
     * @return ���s����
     */
    public static boolean takeMovie() {
        return takeMovie(0);
    }

    /**
     * ������B���ă��f�B�A�C���[�W���i�[
     * @param quality �掿
     * @return ���s����
     */
    public static boolean takeMovie(final int quality) {

        try {
        	// �G���[�`�F�b�N
        	if (MOV_SIZE.length < quality) {
                return false;
        	}

            // �w�肳�ꂽ�掿��ݒ肷��
            camera.setImageSize(MOV_SIZE[quality][0], MOV_SIZE[quality][1]);

            // �ʐ^���B��
            camera.takeMovie();

            // �B�e����Ȃ��ꍇ
            if (camera.getNumberOfImages() == 0) {
                return false;
            }

            type = MOVIE;
            return true;
        } catch (Exception e) {
           	dialog.setText("���݁A����B�e���ł��Ȃ���Ԃł��B\n");
           	dialog.show();
            return false;
        }
    }

    /**
     * �ʐ^���B���ă��f�B�A�C���[�W���i�[
     * @return ���s����
     */
    public static boolean takePicture() {
        return takePicture(0);
    }

    /**
     * �ʐ^���B���ă��f�B�A�C���[�W���i�[
     * @param quality �掿
     * @return ���s����
     */
    public static boolean takePicture(final int quality) {

        try {

        	// �G���[�`�F�b�N
        	if (PIC_SIZE.length < quality) {
                return false;
        	}

            // �w�肳�ꂽ�掿��ݒ肷��
            camera.setImageSize(PIC_SIZE[quality][0], PIC_SIZE[quality][1]);

            // �ʐ^���B��
            camera.takePicture();

            // �B�e����Ȃ��ꍇ
            if (camera.getNumberOfImages() == 0) {
                return false;
            }

            type = PICTURE;
            return true;
        } catch (Exception e) {
           	dialog.setText("���݁A�ʐ^�B�e���ł��Ȃ���Ԃł��B\n");
           	dialog.show();
            return false;
        }
    }

    /**
     * �ʐ^��I�����f�B�A�C���[�W���i�[
     * @return ���s����
     */
    public static boolean selectPicture() {

        try {
        	// �t�@�C������ǂݍ���
        	image = ImageStore.selectEntry();
            type = PHOTO;
            return true;
        } catch (Exception e) {
           	dialog.setText("���݁A�ʐ^�I�����ł��Ȃ���Ԃł��B\n");
           	dialog.show();
            return false;
        }
    }

    /**
     * �����I�����f�B�A�C���[�W���i�[
     * @return ���s����
     */
    public static boolean selectMovie() {

        try {
        	// �t�@�C������ǂݍ���
		    movie = MovieStore.selectEntry();
            type = VIDEO;
            return true;
        } catch (Exception e) {
           	dialog.setText("���݁A����I�����ł��Ȃ���Ԃł��B\n");
           	dialog.show();
            return false;
        }
    }

    /**
     * �R���X�g���N�^
     */
    private CameraDevice() {
    }
}
