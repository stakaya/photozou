import com.nttdocomo.ui.Canvas;
import com.nttdocomo.ui.Dialog;
import com.nttdocomo.ui.Display;
import com.nttdocomo.ui.Graphics;
import com.nttdocomo.ui.IApplication;
import com.nttdocomo.ui.Image;
import com.nttdocomo.ui.MediaImage;
import com.nttdocomo.ui.MediaManager;
import com.nttdocomo.ui.TextBox;
import com.nttdocomo.ui.VisualPresenter;

/**
 * Photozo<BR>
 * アプリケーションのメインクラスです。.
 * <br>
 * <p>
 * @version 1.0
 * </p>
 */
public final class Photozo extends IApplication {

    /**
     * <code>screen</code> メイン画面
     */
    private static Canvas screen = null;

    /**
     * <code>ENTRY_CGI</code> 登録CGI
     */
    public static final String ENTRY_CGI = "Entry.cgi";

    /**
     * <code>AUTH_CGI</code> 認証CGI
     */
    public static final String AUTH_CGI = "Auth.cgi";

    /**
     * <code>TYPE_SOUND</code> 音声識別子
     * <code>TYPE_MOVIE</code> 動画識別子
     * <code>TYPE_PICTURE</code> 静止画識別子
     */
    public static final String
        TYPE_SOUND   = "WAV",
        TYPE_MOVIE   = "MOV",
        TYPE_PICTURE = "PIC";

    /**
     * <code>PINK  </code> 表示色「ピンク」を表す定数
     * <code>ORANGE</code> 表示色「オレンジ」を表す定数
     * <code>WHITE </code> 表示色「白」を表す定数
     * <code>LIME  </code> 表示色「ライム」を表す定数
     * <code>SILVER</code> 表示色「銀」を表す定数
     * <code>BLACK </code> 表示色「黒」を表す定数
     * <code>YELLOW</code> 表示色「黄色」を表す定数
     * <code>GRAY  </code> 表示色「灰色」を表す定数
     * <code>RED   </code> 表示色「赤」を表す定数
     * <code>BLUE  </code> 表示色「青」を表す定数
     */
    public static final int
        PINK   = Graphics.getColorOfRGB(248, 222, 194),
        ORANGE = Graphics.getColorOfRGB(255, 128, 0),
        WHITE  = Graphics.getColorOfName(Graphics.WHITE) ,
        LIME   = Graphics.getColorOfName(Graphics.LIME)  ,
        SILVER = Graphics.getColorOfName(Graphics.SILVER),
        BLACK  = Graphics.getColorOfName(Graphics.BLACK) ,
        YELLOW = Graphics.getColorOfName(Graphics.YELLOW),
        GRAY   = Graphics.getColorOfName(Graphics.GRAY)  ,
        RED    = Graphics.getColorOfName(Graphics.RED)   ,
        BLUE   = Graphics.getColorOfName(Graphics.BLUE);

    /**
     * アプリケーションが起動したら最初に呼ばれるメソッドです。
     */
    public void start() {
        try {
            // 保存データ読み出し
            ScratchPad.load();

            // スクラッチパッドにデータ保存されていいなかったら？
            if(ScratchPad.length() == 0) {
                // メイン画面の作成
                screen = new StartScreen();
            } else {
             	// スタート画面イメージ取得
                MediaData mail = ScratchPad.getMediaData(0);
                MediaData pass = ScratchPad.getMediaData(1);
                
                if (mail == null || pass == null) {
                    // メイン画面の作成
                    screen = new StartScreen();
                } else {
                    // メイン画面の作成
                    screen = new MainScreen(mail.getText(), pass.getText());
                }
            }

            // メイン画面の表示
            Display.setCurrent(screen);
        } catch (Exception e) {
            Dialog dialog = new Dialog(Dialog.DIALOG_INFO, "障害情報");
            dialog.setText(e.getClass().getName());
            dialog.show();
            terminate();
        }
    }

    /**
     * MainScreen<BR>
     * メイン画面の定義クラスです。
     * <p>
     * @version 1.0
     * </p>
     */
    private final class MainScreen extends Canvas {

        /**
         * <code>unSupportMovie</code> ムービーをサポートしない機種
         */
        private final String[] unSupportMovie = {
            "F900i",
            "F901i",
            "P900i",
            "P901i",
            "N900i",
            "N901i",
            "SH900i",
            "SH901i",
            "D800iDS",
            "M702iG",
            "M702iS"
        };

        /**
         * <code>back</code> 背景画像
         */
        private Image back = null;

        /**
         * <code>comment</code> コメント
         */
        private String comment = "";

        /**
         * <code>albumName</code> アルバム名
         */
        private String[] albumName;

        /**
         * <code>album</code> アルバム
         */
        private int album = 0;

        /**
         * <code>albumId</code> アルバムID
         */
        private String[] albumId;

        /**
         * <code>function</code> 機能
         */
        private int function = 0;

        /**
         * <code>functionList</code> 機能リスト
         */
        private String[] functionList;

        /**
         * <code>mode</code> モード(メニュー選択)
         */
        private int mode = 0;

        /**
         * <code>quality</code> 画質
         */
        private int quality = 0;

        /**
         * <code>qualityList</code> 画質リスト
         */
        private String[] qualityList;

        /**
         * <code>visual</code> 動画再生イメージ
         */
        private VisualPresenter visual = new VisualPresenter();

        /**
         * <code>mail</code> メール
         */
        private String mail = "";

        /**
         * <code>password</code> パスワード
         */
        private String password = "";

        /**
         * <code>arrow</code> 矢印
         */
        private Image arrow = null;

        /**
         * コンストラクタ
         * @param mail メール
         * @param password パスワード
         */
        public MainScreen(final String mail, final String password) {
            super();
            // データ送信
            String result = Communication.authorize(getSourceURL() + AUTH_CGI, mail, password);
            if (result == null) {
                Dialog dialog = new Dialog(Dialog.DIALOG_WARNING, "ユーザ認証");
                dialog.setText("ユーザ認証でエラーが発生しました。\nユーザ情報を再設定してください。\n");
                dialog.show();

                ScratchPad.removeAll();
                ScratchPad.save();
                terminate();
            }

            String[] temp = Communication.split(result, "\n");
            albumId = new String[temp.length / 2];
            albumName = new String[temp.length / 2];

            for (int i = 0, n = 1; i < albumId.length; i++) {
                albumId[i] = temp[n++].trim();
                albumName[i] = temp[n++].trim();
            }

            this.mail = mail;
            this.password = password;
            this.initScreen();
        }

        /**
         * コンストラクタ
         * @param mail メール
         * @param password パスワード
         * @param album アルバム情報
         */
        public MainScreen(final String mail, final String password, final String album) {
            super();
            String[] temp = Communication.split(album, "\n");
            albumId = new String[temp.length / 2];
            albumName = new String[temp.length / 2];

            for (int i = 0, n = 1; i < albumId.length; i++) {
                albumId[i] = temp[n++];
                albumName[i] = temp[n++];
            }

            this.mail = mail;
            this.password = password;
            this.initScreen();
        }

        /**
         * 画面初期化処理
         */
        private void initScreen() {

            String[] args = IApplication.getCurrentApp().getArgs();
            String phoneName = System.getProperty("microedition.platform");
            boolean isSupport = true;

            try {
                MediaData md = ScratchPad.getResource(0);
                if (md != null) {
                    MediaImage mi = MediaManager.getImage(md.toByteArray());
                    mi.use();
                    back = mi.getImage();
                }

                md = ScratchPad.getResource(5);
                if (md != null) {
                    MediaImage mi = MediaManager.getImage(md.toByteArray());
                    mi.use();
                    arrow = mi.getImage();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 機種判別
            for (int i = 0; i < unSupportMovie.length; i++) {
                if (phoneName.indexOf(unSupportMovie[i]) != -1) {
                    isSupport = false;
                    break;
                }
            }

            // 動画がサポートされている場合
            if (isSupport) {
                functionList = new String[4];
                functionList[0] = (char) 0xE681 + " 撮る";
                functionList[1] = (char) 0xE677 + " 撮る";
                functionList[2] = (char) 0xE681 + " 選ぶ";
                functionList[3] = (char) 0xE677 + " 選ぶ";
            } else {
                functionList = new String[3];
                functionList[0] = (char) 0xE681 + " 撮る";
                functionList[1] = (char) 0xE677 + " 撮る";
                functionList[2] = (char) 0xE681 + " 選ぶ";
            }

            // カメラサイズ取得
            qualityList = new String[CameraDevice.PIC_SIZE.length];
            for (int i = 0; i < CameraDevice.PIC_SIZE.length; i++) {
                qualityList[i] = Integer.toString(CameraDevice.PIC_SIZE[i][0])
                               + " * "
                               + Integer.toString(CameraDevice.PIC_SIZE[i][1]);
            }
        }

        /**
         * 上下キーが押された時に呼び出されるメソッドです。.
         * <br>
         * @param param パラメータ
         */
        private void componentActionLeftRight(final int param) {

            // 撮影画像確認
            if (CameraDevice.getNumberOfImages() > 0) {
                return;
            }

            // アルバムの場合
            if (this.mode == 0) {
                // 左キーの場合
                if (param == Display.KEY_LEFT) {
                    if (0 == this.album) {
                        this.album = this.albumName.length - 1;
                    } else {
                        this.album--;
                    }

                // 右キーの場合
                } else if (param == Display.KEY_RIGHT) {
                    if (this.albumName.length == (this.album + 1)) {
                        this.album = 0;
                    } else {
                        this.album++;
                    }
                }

            // 機能の場合
            } else if (this.mode == 1) {
                // 左キーの場合
                if (param == Display.KEY_LEFT) {
                    if (this.function == 0) {
                        this.function = this.functionList.length - 1;
                    } else {
                        this.function--;
                    }

                // 右キーの場合
                } else if (param == Display.KEY_RIGHT) {
                    if (this.functionList.length == (function + 1)) {
                    	this.function = 0;
                    } else {
                    	this.function++;
                    }
                }

                // 画質クリア
                this.quality = 0;

                // 静止画の場合
                if (this.function == 0) {
                    this.qualityList = new String[CameraDevice.PIC_SIZE.length];
                    for (int i = 0; i < CameraDevice.PIC_SIZE.length; i++) {
                        this.qualityList[i] = Integer.toString(CameraDevice.PIC_SIZE[i][0])
                                            + " * "
                                            + Integer.toString(CameraDevice.PIC_SIZE[i][1]);
                    }

                // 動画の場合
                } else if (this.function == 1) {
                    this.qualityList = new String[CameraDevice.MOV_SIZE.length];
                    for (int i = 0; i < CameraDevice.MOV_SIZE.length; i++) {
                        this.qualityList[i] = Integer.toString(CameraDevice.MOV_SIZE[i][0])
                                             + " * "
                                             + Integer.toString(CameraDevice.MOV_SIZE[i][1]);
                    }

                // 選択の場合
                } else {
                    this.qualityList = new String[1];
                    this.qualityList[0] = "---------";
                }
            	
            // サイズの場合
            } else if (this.mode == 2) {
                // 左キーの場合
                if (param == Display.KEY_LEFT) {
                    if (quality == 0) {
                        this.quality = this.qualityList.length - 1;
                    } else {
                        this.quality--;
                    }

                // 右キーの場合
                } else if (param == Display.KEY_RIGHT) {
                    if (this.qualityList.length == (quality + 1)) {
                        this.quality = 0;
                    } else {
                        this.quality++;
                    }
                }
            }
        }

        /**
         * 上下キーが押された時に呼び出されるメソッドです。.
         * <br>
         * @param param パラメータ
         */
        private void componentActionUpDown(final int param) {

        	// 撮影画像確認
            if (CameraDevice.getNumberOfImages() > 0) {
                return;
            }

            // 上キーの場合
            if (param == Display.KEY_UP) {
                if (this.mode == 0) {
                    this.mode = 2;
                } else {
                    this.mode--;
                }

            // 下キーの場合
            } else if (param == Display.KEY_DOWN) {
                if (this.mode == 2) {
                    this.mode = 0;
                } else {
                    this.mode++;
                }
            }
        }

        /**
         * 描画イベント
         * @see com.nttdocomo.ui.Canvas#paint(com.nttdocomo.ui.Graphics)
         */
        public void paint(final Graphics g) {

            // 描画ストップ
            g.lock();

            if (back != null) {
                g.drawImage(back, 0, 0);
            }

            if (arrow != null) {
                if (this.mode == 0) {
                    g.drawImage(arrow, 215, 179);
                } else if (this.mode == 1) {
                    g.drawImage(arrow, 130, 201);
                } else if (this.mode == 2) {
                    g.drawImage(arrow, 130, 221);
                }
            }

            // コメントが長い場合
            g.setColor(GRAY);
            if (this.comment.length() > 14) {
                g.drawString(this.comment.substring(0, 14), 55, 170);
            } else {
                g.drawString(this.comment, 55, 170);
            }

            // 撮影画像確認
            if (CameraDevice.getNumberOfImages() > 0) {
                // 枠表示
                g.fillRect(54, 2, 152, 152);

                // ソフトキーラベルの設定
                setSoftLabel(SOFT_KEY_1, "消す");
                setSoftLabel(SOFT_KEY_2, "送る");
            } else {
                // ソフトキーラベルの設定
                setSoftLabel(SOFT_KEY_1, "終り");
                if (this.function == 0 || this.function == 1) {
                    setSoftLabel(SOFT_KEY_2, "撮る");
                } else {
                    setSoftLabel(SOFT_KEY_2, "選ぶ");
                }
            }

            // カメライメージ取得
            Image image = null;
            if (this.function == 0 || this.function == 2) {
                image = CameraDevice.getCameraImage();
            }

            // イメージが取得できた場合
            if (image != null) {
                int x = 0;
                int y = 0;
                int width = 150;
                int height = 150;
                int imageWidth  = width;
                int imageHeight = height;

                // 横長の場合
                if (image.getWidth() * height < image.getHeight() * width) {
                   imageWidth  = image.getWidth()  * height / image.getHeight();
                    x = (width - imageWidth) / 2;
                }

                // 縦長の場合
                if (image.getWidth() * height > image.getHeight() * width) {
                   imageHeight = image.getHeight() * width / image.getWidth();
                    y = (height - imageHeight) / 2;
                }

                // 画像表示
                g.drawScaledImage(image,
                                  x + 55, y + 3,
                                  imageWidth, imageHeight,
                                  0, 0,
                                  image.getWidth(), image.getHeight());
            }

            // 機能描画
            g.setColor(GRAY);
            g.setPictoColorEnabled(true);
            g.drawString(this.functionList[this.function], 70, 212);
            g.setPictoColorEnabled(false);
            // サイズ描画
            g.setColor(GRAY);
            g.drawString(this.qualityList[this.quality], 64, 234);
            g.drawString(this.albumName[this.album], 55, 191);

            // 描画
            g.unlock(true);
        }

        /**
         * キーイベント
         * @see com.nttdocomo.ui.Canvas#processEvent(int, int)
         */
        public void processEvent(final int type, final int param) {
            if (type == Display.KEY_RELEASED_EVENT) {
                switch(param) {

                // 右左キー
                case Display.KEY_LEFT:
                case Display.KEY_RIGHT:
                    this.componentActionLeftRight(param);
                    this.repaint();
                    break;

                // 上下キー
                case Display.KEY_DOWN:
                case Display.KEY_UP:
                    this.componentActionUpDown(param);
                    this.repaint();
                    break;

                // ソフトキー
                case Display.KEY_SOFT1:
                case Display.KEY_SOFT2:
                    this.softKeyReleased(param);
                    this.repaint();
                    break;

                // 決定キー
                case Display.KEY_SELECT:
                    imeOn(this.comment, TextBox.DISPLAY_ANY, TextBox.KANA);
                    break;

                // 数字の１キー
                case Display.KEY_1:
                	launch(LAUNCH_BROWSER, new String[]{"http://m.photozou.jp/"});
                    break;

                // 数字の２キー
                case Display.KEY_2:
                    if (this.function == 1 || this.function == 3) {
                        // カメライメージ取得
                        MediaImage image = CameraDevice.getMediaImage();

                        // イメージが取得できた場合
                        if (image != null) {
                            visual.setImage(image);
                            visual.play();
                        }
                    }
                    break;
                default:
                }
            }
        }

        /**
         * IMEイベント
         * @see com.nttdocomo.ui.Canvas#processIMEEvent(int, java.lang.String)
         */
        public void processIMEEvent(final int type, final String text) {
            if (type == IME_COMMITTED) {
                this.comment = text;
                this.repaint();
            }
        }

        /**
         * 画像を送信する。
         */
        private void send() {
            boolean result = false;
            MediaData md = null;

            // 動画・静止画を判定
            if (this.mode == 1 || this.mode == 3) {
                // メディアデータに入れる
                md = new MediaData(CameraDevice.getCameraData(),
                                   TYPE_MOVIE,
                                   this.comment);
            } else {
                // メディアデータに入れる
                md = new MediaData(CameraDevice.getCameraData(),
                                   TYPE_PICTURE,
                                   this.comment);
            }

            // データ送信
            result = Communication.sendData(
                         getSourceURL() + ENTRY_CGI,
                         md.getType(),
                         this.mail,
                         this.password,
                         this.albumId[this.album],
                         this.comment,
                         md.toInputStream());

            // 通信結果判定
            if (result) {
                // 残っているデータを削除
                CameraDevice.dispose();
            } else {
                Dialog dialog = null;
                dialog = new Dialog(Dialog.DIALOG_WARNING, "通信障害");
                dialog.setText("通信中にエラーが発生しました。\n電波状況の良いところで再送ください。\n");
            }

            // 画面を戻す
            this.show();
        }

        /**
         * 画面を表示
         */
        public void show() {
            Display.setCurrent(this);
        }

        /**
         * 押下されたソフトキーが離された時に呼び出されるメソッドです。.
         * <br>
         * このメソッドは、<code>setSoftKeyListener()</code>を使って
         * ソフトキーリスナーを登録している場合に有効となります。
         * @param key 離されたキー
         */
        private void softKeyReleased(final int key) {

            // 終了キー押下
            if (Display.KEY_SOFT1 == key && CameraDevice.getNumberOfImages() == 0) {

            	// アプリ終了
                IApplication.getCurrentApp().terminate();

            // 撮影キー押下
            } else if (Display.KEY_SOFT2 == key && CameraDevice.getNumberOfImages() == 0) {
                // 動画・静止画を判定
                if (this.function == 0) {
                    CameraDevice.takePicture(this.quality);
                } else if (this.function == 1) {
                    CameraDevice.takeMovie(this.quality);
                } else if (this.function == 2) {
                    CameraDevice.selectPicture();
                } else {
                    CameraDevice.selectMovie();
                }

            // 取消キー押下
            } else if (Display.KEY_SOFT1 == key && CameraDevice.getNumberOfImages() != 0) {
                // 残っているデータを削除
                CameraDevice.dispose();

            // 送信キー押下
            } else if (Display.KEY_SOFT2 == key && CameraDevice.getNumberOfImages() != 0) {
                // データ送信
                this.send();
            }
        }
    }
    
    /**
     * StartScreen<BR>
     * 開始画面の定義クラスです。
     * <p>
     * @version 1.0
     * </p>
     */
    private final class StartScreen extends Canvas {

        /**
         * <code>back</code> 背景画像
         */
        private Image back = null;

        /**
         * <code>arrow</code> 矢印
         */
        private Image arrow = null;

        /**
         * <code>mail</code> メール
         */
        private String mail = "";

        /**
         * <code>password</code> パスワード
         */
        private String password = "";

        /**
         * <code>mode</code> モード(メール・パスワード/選択)
         */
        private boolean mode = true;

        /**
         * コンストラクタ
         */
        public StartScreen() {
            super();

            try {

            	// スタート画面イメージ取得
                MediaData md = ScratchPad.getResource(3);
                if (md != null) {
                    MediaImage mi = MediaManager.getImage(md.toByteArray());
                    mi.use();
                    back = mi.getImage();
                }
                
                // 矢印イメージ取得
                md = ScratchPad.getResource(4);
                if (md != null) {
                    MediaImage mi = MediaManager.getImage(md.toByteArray());
                    mi.use();
                    arrow = mi.getImage();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * 上下キーが押された時に呼び出されるメソッドです。.
         * <br>
         * @param param パラメータ
         */
        private void componentActionUpDown(final int param) {

            // 上下キーの場合
            if (param == Display.KEY_UP || param == Display.KEY_DOWN) {
            	this.mode = !this.mode;
            }
        }

        /**
         * 描画イベント
         * @see com.nttdocomo.ui.Canvas#paint(com.nttdocomo.ui.Graphics)
         */
        public void paint(final Graphics g) {

            // 描画ストップ
            g.lock();

            if (back != null) {
                g.drawImage(back, 0, 0);
            }

            if (arrow != null) {
                if (this.mode) {
                    g.drawImage(arrow, 225, 173);
                } else {
                    g.drawImage(arrow, 225, 205);
                }
            }

            // コメントが長い場合
            g.setColor(GRAY);
            setSoftLabel(SOFT_KEY_1, "終る");
            setSoftLabel(SOFT_KEY_2, "送る");

            // 機能描画
            g.drawString(this.mail, 60, 186);
            if (this.password.length() > 0) {
                g.drawString("**************", 60, 217);
            }

            // 描画
            g.unlock(true);
        }

        /**
         * キーイベント
         * @see com.nttdocomo.ui.Canvas#processEvent(int, int)
         */
        public void processEvent(final int type, final int param) {
            if (type == Display.KEY_RELEASED_EVENT) {
                switch(param) {

                // 上下キー
                case Display.KEY_DOWN:
                case Display.KEY_UP:
                    this.componentActionUpDown(param);
                    this.repaint();
                    break;

                // ソフトキー
                case Display.KEY_SOFT1:
                case Display.KEY_SOFT2:
                    this.softKeyReleased(param);
                    this.repaint();
                    break;

                // 決定キー
                case Display.KEY_SELECT:
                    if (this.mode) {
                        imeOn(this.mail, TextBox.DISPLAY_ANY, TextBox.ALPHA);
                    } else {
                        imeOn(this.password, TextBox.DISPLAY_ANY, TextBox.ALPHA);
                    }

                    break;
                default:
                }
            }
        }

        /**
         * IMEイベント
         * @see com.nttdocomo.ui.Canvas#processIMEEvent(int, java.lang.String)
         */
        public void processIMEEvent(final int type, final String text) {
            if (type == IME_COMMITTED) {
            	
                if (this.mode) {
                    this.mail = text;
                } else {
                    this.password = text;
                }
            	
                this.repaint();
            }
        }

        /**
         * 押下されたソフトキーが離された時に呼び出されるメソッドです。.
         * <br>
         * このメソッドは、<code>setSoftKeyListener()</code>を使って
         * ソフトキーリスナーを登録している場合に有効となります。
         * @param key 離されたキー
         */
        private void softKeyReleased(final int key) {

            // 取消キー押下
            if (Display.KEY_SOFT1 == key) {
                // アプリ終了
                IApplication.getCurrentApp().terminate();
            // 送信キー押下
            } else if (Display.KEY_SOFT2 == key) {
            	send();
            }
        }
        
        /**
         * 認証を送信する。
         */
        private void send() {
            // データ送信
            String result = Communication.authorize(getSourceURL() + AUTH_CGI, this.mail, this.password);

            if (result == null) {
                Dialog dialog = new Dialog(Dialog.DIALOG_WARNING, "ユーザ認証");
                dialog.setText("ユーザ認証でエラーが発生しました。\nアカウントが有効か確認してください。\n");
                dialog.show();
                // 画面を戻す
                this.show();
            } else {
            	ScratchPad.addImage(new MediaData(this.mail));
            	ScratchPad.addImage(new MediaData(this.password));
            	if (ScratchPad.save()){
                    // メイン画面の作成
                    screen = new MainScreen(this.mail, this.password, result);
                    // メイン画面の表示
                    Display.setCurrent(screen);
            	}
            }
        }
        
	    /**
	     * 画面を表示
	     */
	    public void show() {
	        Display.setCurrent(this);
	    }
	}
}
