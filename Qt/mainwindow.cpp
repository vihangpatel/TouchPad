#include "mainwindow.h"
#include "ui_mainwindow.h"
#include <QTest>
#include <QtTest/QTestMouseEvent>
#include <windows.h>

static  QString MOUSE_CLICK = "0";
static  QString MOUSE_MOVE = "1";
static  QString WHEEL = "2" ;

static  QString LEFT_BUTTON = "100";
static  QString RIGHT_BUTTON ="101";

static QString LONG_HOLD = "11";
static QString SINGLE_TAP = "12";

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    int portNo = 9000;
    m_poSocket = new QTcpSocket();
    m_poServer = new QTcpServer();
    m_HostAddress = new QHostAddress("192.168.43.8");

    if(!m_poServer->listen(*m_HostAddress,portNo))
    {
        QMessageBox::critical(this,"Port No " + QString::number( portNo )+ "is already in use."
                              ,"Port number " + QString::number( portNo ) + " is used by some another application"
                              ,QMessageBox::Ok);
        return ;
    }

    connect(m_poServer,SIGNAL(newConnection()),this,SLOT(ConnectionHandler()));
}

void MainWindow::ConnectionHandler()
{
    m_poSocket = m_poServer->nextPendingConnection();
    connect(m_poSocket, SIGNAL(disconnected()),m_poSocket, SLOT(deleteLater()));
    QHostAddress a( m_poSocket->peerAddress());
    QString str = "New connection has been arrived  :" + a.toString() + " Arr :" + QTime::currentTime().toString("hh:mm:ss:zzz") + "\n";
    qDebug() << "connection arrived..." + str;
    connect(m_poSocket,SIGNAL(readyRead()),this,SLOT(Reader()));
}

void MainWindow::Reader()
{
    QDataStream in(m_poSocket);
    in.device()->seek(0);

    QByteArray ary = m_poSocket->readAll();
    QString string(ary);

    qDebug() <<string;
    QStringList string_list = string.split("#");

    if(string_list[0]== MOUSE_MOVE)
    {
        QPoint receivedPt = HandleMouseCursor(string_list[1]);
        QPoint pt = m_cursor.pos();
        int x = receivedPt.x();
        int y = receivedPt.y();
        pt.setX(pt.x()+x*ui->horizontalSlider->value());
        pt.setY(pt.y()+y*ui->horizontalSlider->value());
        m_cursor.setPos(pt);
    }

    if(string_list[0]== MOUSE_CLICK)
    {
         HandleMouseButtonPress(string_list[1]);
    }

    if(string_list[0] == WHEEL)
    {
        QPoint receivedPt = HandleMouseCursor(string_list[1]);
        int x = receivedPt.x();
        int y = receivedPt.y();
        mouse_event(MOUSEEVENTF_WHEEL | WHEEL_PAGESCROLL,x,y,3,0);
        qDebug() << "Wheel event is coming ... ";
    }

    m_poSocket->disconnectFromHost();

}

void MainWindow::HandleMouseButtonPress(QString buttonType)
{

    QStringList stringList = buttonType.split("@");
    if(stringList[0] == SINGLE_TAP)
    {
        if(stringList[1] == LEFT_BUTTON)
        {
            mouse_event(MOUSEEVENTF_ABSOLUTE | MOUSEEVENTF_LEFTDOWN | MOUSEEVENTF_LEFTUP, 1, 1, 0, 0);
        }
        if(stringList[1] == RIGHT_BUTTON)
        {
            mouse_event(MOUSEEVENTF_ABSOLUTE | MOUSEEVENTF_RIGHTDOWN | MOUSEEVENTF_RIGHTUP, 1, 1, 0, 0);
        }
    }

    if(stringList[0] == LONG_HOLD)
    {
        mouse_event(MOUSEEVENTF_ABSOLUTE | MOUSEEVENTF_LEFTDOWN , 1, 1, 0, 0);
    }


}

QPoint MainWindow::HandleMouseCursor(QString coordinateString)
{
    QStringList string_list = coordinateString.split("@");

    int x = string_list[0].toInt();
    int y = string_list[1].toInt();

    QPoint pt ;
    pt.setX(x);
    pt.setY(y);

    return pt;
}

MainWindow::~MainWindow()
{
    delete ui;
}
