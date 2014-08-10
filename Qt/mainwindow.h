#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QTcpSocket>
#include <QTcpServer>
#include <QMessageBox>
#include <QTime>
#include <QString>
#include <QMouseEvent>
#include <QCoreApplication>

namespace Ui {
    class MainWindow;
}

class Constant
{


};

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:



    QTimer *mpo_Timer;
    QTcpSocket *m_poSocket;
    QTcpServer *m_poServer;
    QHostAddress *m_HostAddress ;
    QCursor m_cursor ;

    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();

public slots :
    void ConnectionHandler();
    void Reader();
    void HandleMouseButtonPress(QString buttonType);
    QPoint HandleMouseCursor(QString coordinateString );

private:
    Ui::MainWindow *ui;
};

#endif // MAINWINDOW_H
