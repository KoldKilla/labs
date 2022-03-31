package com.company;
import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class FractalExplorer {

    //поля для отключения UI
    private JButton saveButton;
    private JButton resetButton;
    private JComboBox myComboBox;

    //сколько осталось рядов
    private int rowsRemaining;

    //размер дисплея
    private int displaySize;

    //ссылка для обнолвления дисплей после измерения фрактала
    private JImageDisplay display;

    //объект для генератора фракталов
    private FractalGenerator fractal;

    //объект, который указывает диапазон, который мы сейчас показываем
    private Rectangle2D.Double range;

    //конструктор для установки размера дисплея и инициализаии объектов для генератора фрактала
    public FractalExplorer(int size) {
        //запоминаю размер дисплея
        displaySize = size;

        //инициализирую генератор фрактала и объекты
        fractal = new Mandelbrot();
        range = new Rectangle2D.Double();
        fractal.getInitialRange(range);
        display = new JImageDisplay(displaySize, displaySize);
    }

    //создаём GUI(графикал юзер интерфейс)
    public void createAndShowGUI() {

        //устанавливаем рамку
        display.setLayout(new BorderLayout());
        JFrame myFrame = new JFrame("Fractal Explorer");

        //добавляем место для картинки и центрируем его
        myFrame.add(display, BorderLayout.CENTER);

        //создаём кнопку ресет (возвращает картинку в исходное состояния)
        resetButton = new JButton("Reset");

        //экземляр кнопки сброса и добавление ему активности
        ButtonHandler resetHandler = new ButtonHandler();
        resetButton.addActionListener(resetHandler);

        //экземпляр клика мышкой по фракталу и добавление ему активности
        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);

        //крестик - закрыть и остановить программу
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //сделал комбобокс
        myComboBox = new JComboBox();

        //заполнил его (toString)
        FractalGenerator mandelbrotFractal = new Mandelbrot();
        myComboBox.addItem(mandelbrotFractal);
        FractalGenerator tricornFractal = new Tricorn();
        myComboBox.addItem(tricornFractal);
        FractalGenerator burningShipFractal = new BurningShip();
        myComboBox.addItem(burningShipFractal);

        //что делать комбобоксу при нажатии
        ButtonHandler fractalChooser = new ButtonHandler();
        myComboBox.addActionListener(fractalChooser);

        //создаю верхнеюю панель, добавляю лейбл(место для текста) и устанавливаю его сверху а ещё добавляю комбобокс
        JPanel myTopPanel = new JPanel();
        JLabel myLabel = new JLabel("Fractal:");
        myTopPanel.add(myLabel);
        myTopPanel.add(myComboBox);
        myFrame.add(myTopPanel, BorderLayout.NORTH);

        //создаю кнопку сохранить
        saveButton = new JButton("Save");

        //создаю нижнюю панель и добавляю на неё кнопку ресета и сейва
        JPanel myBottomPanel = new JPanel();
        myBottomPanel.add(resetButton);
        myBottomPanel.add(saveButton);
        myFrame.add(myBottomPanel, BorderLayout.SOUTH);

        //заполняю эту кнопку
        ButtonHandler saveHandler = new ButtonHandler();
        saveButton.addActionListener(saveHandler);

        //располагаю содержимое на рамке, делаю это видиым и запрещаю изменять размер окна
        myFrame.pack();
        myFrame.setVisible(true);
        myFrame.setResizable(false);

    }

    //метод для рисования фрактала
    private void drawFractal() {
        //отключаю интерфейс пользователя
        enableUI(false);

        //устанаваливает количество оставшихся строк равным количеству строк дисплея
        rowsRemaining = displaySize;

        //прохожу через каждый ряд и рисую его через ФракталВоркер
        for (int x=0; x<displaySize; x++){
            FractalWorker drawRow = new FractalWorker(x);
            drawRow.execute();
        }
    }

    //включение и отключение UI
    private void enableUI(boolean val) {
        myComboBox.setEnabled(val);
        resetButton.setEnabled(val);
        saveButton.setEnabled(val);
    }

    //метод для определения действия при нажатии на кнопки
    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //получаю источник действия
            String command = e.getActionCommand();

            //если источник - кнопка ресет, то делаю заново фрактал
            if (command.equals("Reset")) {
                fractal.getInitialRange(range);
                drawFractal();
            }

            //если источник - инстанс оф комбобокса, то выбираю другой фрактал
            else if (e.getSource() instanceof JComboBox) {
                JComboBox mySource = (JComboBox) e.getSource();
                fractal = (FractalGenerator) mySource.getSelectedItem();
                fractal.getInitialRange(range);
                drawFractal();
            }
            else if (command.equals("Save")) {

                //позволяет пользователю выбрать файл куда сохранить картинку
                JFileChooser myFileChooser = new JFileChooser();

                //сохраняем только в пнг
                FileFilter extensionFilter = new FileNameExtensionFilter("PNG Images", "png");
                myFileChooser.setFileFilter(extensionFilter);

                //гарантия того, что будет только пнг
                myFileChooser.setAcceptAllFileFilterUsed(false);

                //вылезает окно с выбором места для сохранения файла
                int userSelection = myFileChooser.showSaveDialog(display);

                //если окно появилось, то мы идём дальше по сохранению
                if (userSelection == JFileChooser.APPROVE_OPTION) {

                    //ввожу имя файла
                    java.io.File file = myFileChooser.getSelectedFile();
                    String file_name = file.toString();

                    //пробую сохранить (так как может быть ошибка)
                    try {
                        BufferedImage displayImage = display.getImage();
                        javax.imageio.ImageIO.write(displayImage, "png", file);
                    }
                    //если что-то пошло не так, то вывожу ошибку
                    catch (Exception exception) {
                        JOptionPane.showMessageDialog(display, exception.getMessage(), "ОШИБКА", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else return;
            }

        }
    }

    //класс для клика мышкой
    private class MouseHandler extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e) {

            //проверяем есть ли строки, который ещё должны быть прорисованы
            if (rowsRemaining != 0) {
                return;
            }

            //получаю координаты клика
            int x = e.getX();
            double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);
            int y = e.getY();
            double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);

            //вызываю метод для зума, который делает зум в 2 раза к нужным координатам
            //и таким образом делает изменённый фрактал
            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);

            //перерисовываю фрактал
            drawFractal();
        }
    }

    //рассчитывает цвет для ряда в фрактале
    private class FractalWorker extends SwingWorker<Object, Object> {
        //у координата ряда
        int yCoordinate;

        //массив интовых значений, для цвета пикселей в ряду
        int[] computedRGBValues;

        //получает у как параметр и сохраняет его
        private FractalWorker(int row) {
            yCoordinate = row;
        }

        //метод считает ргб цвет всех пикселей в 1 ряду
        protected Object doInBackground() {

            //задаю массив для 1 строки
            computedRGBValues = new int[displaySize];

            //проходим через все пиксели в ряду
            for (int i = 0; i < computedRGBValues.length; i++) {

                //находим соответствующие координаты xCoord and yCoord в отображаемой области фрактала.
                double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, i);
                double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, yCoordinate);

                //считаем количество итерация для координат в отображаемой зоне фрактала
                int iteration = fractal.numIterations(xCoord, yCoord);

                //если количество итераций -1, то красим в чёрный цвет пиксель в ряду
                if (iteration == -1){
                    computedRGBValues[i] = 0;
                }

                else {
                    //если не -1, то уже красим на основе итераций
                    float hue = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);

                    //обновляем массив новыми значениями
                    computedRGBValues[i] = rgbColor;
                }
            }
            return null;

        }

        //вызывается когда сделана doInBackground()
        protected void done() {

            //прохожу через весь массив с цветами и рисую пиксели по цвету
            for (int i = 0; i < computedRGBValues.length; i++) {
                display.drawPixel(i, yCoordinate, computedRGBValues[i]);
            }
            //перерисовываю то, что нарисовал
            display.repaint(0, 0, yCoordinate, displaySize, 1);

            //уменьшаю количество оставшихся рядов на 1
            rowsRemaining--;

            // проверяю сколько рядов осталось, если 0, то вызывая интерфейс пользователя (UI)
            if (rowsRemaining == 0) {
                enableUI(true);
            }
        }
    }

    //метод мейн для вызова и рисования фрактала
    public static void main(String[] args) {
        FractalExplorer displayExplorer = new FractalExplorer(600);
        displayExplorer.createAndShowGUI();
        displayExplorer.drawFractal();
    }
}