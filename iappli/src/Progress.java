import com.nttdocomo.ui.Canvas;
import com.nttdocomo.ui.Display;
import com.nttdocomo.ui.Graphics;
import com.nttdocomo.ui.Image;
import com.nttdocomo.ui.MediaImage;
import com.nttdocomo.ui.MediaManager;

/**
 * Progress<BR>
 * �󋵉�ʂł��B
 * <br>
 * �����󋵂��v���O���X�o�[�ɕ\�����܂��B
 * <p>
 * @version 1.0
 * </p>
 */
public final class Progress extends Canvas {

    /**
     * <code>current</code> �v���O���X�o�[�̐i��
     */
    private static int current = 0;

    /**
     * <code>maximum</code> �v���O���X�o�[�̍ő�l
     */
    private static int maximum = 1;

    /**
     * <code>message</code> ���b�Z�[�W
     */
    private static String message = "";

    /**
     * <code>progress</code> �v���O���X�o�[
     */
    private static Image progress = null;

    /**
     * <code>sending</code> ���M���摜
     */
    private static Image sending = null;

    /**
     * <code>PINK  </code> �\���F�u�s���N�v��\���萔
     * <code>ORANGE</code> �\���F�u�I�����W�v��\���萔
     * <code>WHITE </code> �\���F�u���v��\���萔
     * <code>BLACK </code> �\���F�u���v��\���萔
     * <code>RED   </code> �\���F�u�ԁv��\���萔
     * <code>BLUE  </code> �\���F�u�v��\���萔
     */
    public static final int
        PINK   = Graphics.getColorOfRGB(248, 222, 194)   ,
        ORANGE = Graphics.getColorOfRGB(255, 128, 0)     ,
        WHITE  = Graphics.getColorOfName(Graphics.WHITE) ,
        BLACK  = Graphics.getColorOfName(Graphics.BLACK) ,
        RED    = Graphics.getColorOfName(Graphics.RED)   ,
        BLUE   = Graphics.getColorOfName(Graphics.BLUE);

    /**
     * �R���X�g���N�^.
     * @param picture �摜���[�h
     */
    public Progress(final boolean picture) {

    	if (!picture) {
    		return;
    	}

        try {
        	// �v���O���X�o�[�̉摜���擾
        	MediaData md = ScratchPad.getResource(1);
        	if (md != null) {
                MediaImage mi = MediaManager.getImage(md.toByteArray());
                mi.use();
                progress = mi.getImage();
        	}

        	// �w�i���̉摜���擾
        	md = ScratchPad.getResource(2);
        	if (md != null) {
                MediaImage mi = MediaManager.getImage(md.toByteArray());
                mi.use();
                sending = mi.getImage();
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * �v���O���X�o�[�̐i����i�߂܂��B
     */
    public void add() {
        current++;
        this.repaint();
    }

    /**
     * �v���O���X�o�[��\������B
     * @param g �O���t�B�b�N
     */
    public void paint(final Graphics g) {
        g.lock();
        if (sending != null && progress != null) {
            g.drawImage(sending, 0, 0);
            g.drawImage(progress, 15, 135);
        } else {
            g.setColor(PINK);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        g.setColor(BLACK);
        g.drawString(message, 30, 152);
        g.setColor(WHITE);
        g.fillRect(30, 163, 177, 14);
        g.setColor(ORANGE);
        g.fillRect(30, 163, 177 * current / maximum, 14);
        g.setColor(WHITE);
        g.drawString(current * 100 / maximum + "%", 40, 177);
        g.unlock(true);
    }

    /**
     * �v���O���X�o�[�����������܂��B
     * @param max �v���O���X�o�[�̍ő�l
     */
    public void reset(final int max) {

    	if (max > 0) {
            maximum = max;
    	}

        current = 0;
        message = "";
    }

    /**
     * ���b�Z�[�W��ݒ肵�܂��B
     * @param msg ���b�Z�[�W
     */
    public void setMessage(final String msg) {
        message = msg;
    }

	/**
	 * ��ʂ�\��
	 */
	public void show() {
        Display.setCurrent(this);
	}
}
