package com.company;

import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FractalExplorer
{
    //размер дисплея
    private int displaySize;

    //ссылка для обнолвления дисплей после измерения фрактала
    private JImageDisplay display;

    //объект для генератора фракталов
    private FractalGenerator fractal;

    //объект который указывает диапазон, который мы сейчас показываем
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
    public void createAndShowGUI()
    {
        //устанавливаем рамку
        display.setLayout(new BorderLayout());
        JFrame myFrame = new JFrame("Fractal Explorer");

        //добавляем место для картинки и центрируем его
        myFrame.add(display, BorderLayout.CENTER);

        //создаём кнопку ресет (возвращает картинку в исходное состояния)
        JButton resetButton = new JButton("Reset");

        //экземляр кнопки сброса и добавление ему активности
        ButtonHandler resetHandler = new ButtonHandler();
        resetButton.addActionListener(resetHandler);

        //экземпляр клика мышкой по фракталу и добавление ему активности
        MouseHandler click = new MouseHandler();
        display.addMouseListener(click);

        //крестик - закрыть и остановить программу
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //сделал комбобокс
        JComboBox myComboBox = new JComboBox();

        //заполнил его (toString)
        FractalGenerator mandelbrotFractal = new Mandelbrot();
        myComboBox.addItem(mandelbrotFractal);
        FractalGenerator tricornFractal = new Tricorn();
        myComboBox.addItem(tricornFractal);
        FractalGenerator burningShipFractal = new BurningShip();
        myComboBox.addItem(burningShipFractal);

        //создаю кнопку сохранить
        JButton saveButton = new JButton("Save");

        //заполняю эту кнопку
        ButtonHandler saveHandler = new ButtonHandler();
        saveButton.addActionListener(saveHandler);

        //что делать комбобоксу при нажатии
        ButtonHandler fractalChooser = new ButtonHandler();
        myComboBox.addActionListener(fractalChooser);

        //создаю верхнеюю панель, добавляю лейбл и устанавливаю его сверху а ещё добавляю комбобокс
        JPanel myTopPanel = new JPanel();
        JLabel myLabel = new JLabel("Fractal:");
        myTopPanel.add(myLabel);
        myTopPanel.add(myComboBox);
        myFrame.add(myTopPanel, BorderLayout.NORTH);

        //создаю нижнюю панель и добавляю на неё кнопку ресета и сейва
        JPanel myBottomPanel = new JPanel();
        myBottomPanel.add(resetButton);
        myBottomPanel.add(saveButton);
        myFrame.add(myBottomPanel, BorderLayout.SOUTH);


        //располагаю содержимое на рамке, делаю это видиым и запрещаю изменять размер окна
        myFrame.pack();
        myFrame.setVisible(true);
        myFrame.setResizable(false);

    }

    //метод для рисования фрактала
    private void drawFractal() {
        //проходим через каждый пиксель на доске
        for (int x=0; x<displaySize; x++){
            for (int y=0; y<displaySize; y++){

                //находим соответствующие координаты х и у в фрактальной зоне
                double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);
                double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);

                //считаем количество итерация для координаты в фрактальной зоне
                int iteration = fractal.numIterations(xCoord, yCoord);

                //если колличество итераций -1, то в чёрный цвет
                if (iteration == -1){
                    display.drawPixel(x, y, 0);
                }

                else {

                    //если не -1, то выбираем цвет на основе количества итераий
                    float hue = 0.7f + (float) iteration / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);

                    //добавляем цвета на дисплей
                    display.drawPixel(x, y, rgbColor);
                }

            }
        }
        //когд все пиксели добавлены, перерисовываем дисплей
        display.repaint();
    }

    //метод для определения действия при нажатии на кнопки
    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //получаю источник действия
            String command = e.getActionCommand();

            //если источник - инстанс оф комбобокса, то выбираю другой фрактал
            if (command.equals("Reset")) {
                fractal.getInitialRange(range);
                drawFractal();
            }
            //если источник - кнопка ресет, то делаю заного фрактад
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
                    //если что-то полшло не так, то вывожу ошибку
                    catch (Exception exception) {
                        JOptionPane.showMessageDialog(display, exception.getMessage(), "Cannot Save Image", JOptionPane.ERROR_MESSAGE);
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
            //получаю координаты клика
            int x = e.getX();
            double xCoord = fractal.getCoord(range.x, range.x + range.width, displaySize, x);
            int y = e.getY();
            double yCoord = fractal.getCoord(range.y, range.y + range.height, displaySize, y);

            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);

            drawFractal();
        }
    }

    //метод мейн для вызова и рисования фрактала
    public static void main(String[] args)
    {
        FractalExplorer displayExplorer = new FractalExplorer(600);
        displayExplorer.createAndShowGUI();
        displayExplorer.drawFractal();
    }
}