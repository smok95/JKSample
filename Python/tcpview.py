'''
    네트워크 연결상태 조회

    전체 조회
    psutil.net_connections()

    프로세스 조회
    psutil.Process(pid값).connections()
'''
import psutil

def tcpview():
    strOut = ''
    strFormat = '%-30s\t%-5s\t%-15s\t%-20s\t%-5s\t%-20s\t%-5s\n'
    strOut = strFormat% ('process','pid','status','local address','port','remote address','port')
    strOut += '------------------------------\t-----\t---------------\t--------------------\t-----\t--------------------\t-----\n'
    
    for conn in psutil.net_connections():

        if conn.status == 'NONE':
            continue
        
        proc = psutil.Process(conn.pid)
        name = proc.name()
        pid = conn.pid
        status = conn.status
        local_ip = conn.laddr[0]
        local_port = conn.laddr[1]
        remote_ip = conn.raddr[0] if conn.raddr else ''
        remote_port = conn.raddr[1] if conn.raddr else ''

        strOut += strFormat%(name, pid, status, local_ip, local_port, remote_ip, remote_port)

    print(strOut)


tcpview()


'''
실행결과

c:\Projects\JKSample\Python>tcpview.py
process                         pid     status          local address           port    remote address          port
------------------------------  -----   --------------- --------------------    -----   --------------------    -----
services.exe                    696     LISTEN          ::                      1544
chrome.exe                      10356   ESTABLISHED     192.168.0.8             2185    38.127.167.14           443
TeamViewer_Service.exe          2924    ESTABLISHED     127.0.0.1               5939    127.0.0.1               1592
AnySign4PC.exe                  5536    LISTEN          127.0.0.1               10530
TeamViewer.exe                  5284    ESTABLISHED     127.0.0.1               1596    127.0.0.1               1597
System                          4       LISTEN          ::                      445
chrome.exe                      10356   ESTABLISHED     192.168.0.8             3796    172.217.24.194          80
svchost.exe                     1020    LISTEN          ::                      7680
nosstarter.npe                  7004    LISTEN          0.0.0.0                 14440
wininit.exe                     548     LISTEN          ::                      1536
mDNSResponder.exe               2708    LISTEN          127.0.0.1               5354
AnySign4PCLauncher.exe          2632    LISTEN          127.0.0.1               31026
chrome.exe                      10356   ESTABLISHED     192.168.0.8             3795    172.217.24.194          80
nkrunlite.exe                   12988   CLOSE_WAIT      192.168.0.8             1760    58.229.136.151          443
APSDaemon.exe                   9332    ESTABLISHED     192.168.0.8             1640    17.252.140.104          5223
wininit.exe                     548     LISTEN          0.0.0.0                 1536
jhi_service.exe                 3752    LISTEN          ::1                     1561
iCloudServices.exe              8924    CLOSE_WAIT      192.168.0.8             1628    17.248.157.75           443
svchost.exe                     3408    LISTEN          0.0.0.0                 22
ASDSvc.exe                      2808    CLOSE_WAIT      192.168.0.8             3777    211.115.106.201         80
chrome.exe                      10356   ESTABLISHED     192.168.0.8             3797    104.16.94.65            443
SKCertService.exe               9948    LISTEN          127.0.0.1               14098
nkrunlite.exe                   12988   CLOSE_WAIT      192.168.0.8             1761    58.229.136.151          443
AppleMobileDeviceService.exe    2616    ESTABLISHED     127.0.0.1               1541    127.0.0.1               5354
picpick.exe                     8468    CLOSE_WAIT      192.168.0.8             1619    192.99.63.220           80
TeamViewer_Service.exe          2924    ESTABLISHED     192.168.0.8             1546    37.252.224.2            5938
lsass.exe                       704     LISTEN          0.0.0.0                 1549
StSess.exe                      8300    LISTEN          0.0.0.0                 55920
NSpeedMeter.exe                 2780    LISTEN          0.0.0.0                 12345
'''
