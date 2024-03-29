package com.wxy.utils.sysinfo;

import org.springframework.util.StringUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SystemResourceUtil {

    public static SystemResource getSystemResource(){
        SystemResource systemResource = new SystemResource();
        try {
            SystemInfo si = new SystemInfo();
            HardwareAbstractionLayer hal = si.getHardware();
            OperatingSystem os = si.getOperatingSystem();

            //获取操作系统
            String systemInfo = os.getFamily()+ os.getVersionInfo().getVersion();
            systemResource.setSysInfo(systemInfo);

            //获取系统运行时长
            String upTime = FormatUtil.formatElapsedSecs(os.getSystemUptime());
            systemResource.setUpTime(upTime.replace("days","天"));

            //获取CPU核数
            int cpuCores = hal.getProcessor().getPhysicalProcessorCount();
            systemResource.setCpuCores(cpuCores);

            //获取网卡流量
            long[] beferNetworkRecvAndSents = networkInterfaceRecvAndSent(hal.getNetworkIFs());
            //CPU百分比
            CentralProcessor processor = hal.getProcessor();
            long[] prevTicks = processor.getSystemCpuLoadTicks();
            Util.sleep(1000L);
            double cpuAverage = processor.getSystemCpuLoadBetweenTicks(prevTicks);
            BigDecimal cpuBg = new BigDecimal(cpuAverage*100);
            systemResource.setCpuUsePercent(cpuBg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());

            //内存
            GlobalMemory memory = hal.getMemory();
            long memoryAvailable = memory.getAvailable();
            long memoryTotal = memory.getTotal();
            long memoryUse = memoryTotal-memoryAvailable;
            systemResource.setMemoryTotal(memoryTotal);
            systemResource.setMemoryTotalStr(FormatUtil.formatBytes(memoryTotal));
            systemResource.setMemoryAvailable(memoryAvailable);
            systemResource.setMemoryAvailableStr(FormatUtil.formatBytes(memoryAvailable));
            systemResource.setMemoryUse(memoryUse);
            systemResource.setMemoryUseStr(FormatUtil.formatBytes(memoryUse));
            BigDecimal bg = new BigDecimal(memoryUse*100/memoryTotal);
            systemResource.setMemoryUsePercent(bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());


            //硬盘
            long diskAvailableTotal = 0;
            long diskTotal = 0;

            List<FileSystemInfo> fileSystemInfos = new ArrayList<>();
            FileSystem fileSystem = os.getFileSystem();
            OSFileStore[] fsArray = fileSystem.getFileStores();
            for (OSFileStore fs : fsArray) {
                long usable = fs.getUsableSpace();
                long total = fs.getTotalSpace();
                long use = total-usable;
                FileSystemInfo fileSystemInfo = new FileSystemInfo();
                fileSystemInfo.setName(fs.getName());
                fileSystemInfo.setTotal(total);
                fileSystemInfo.setTotalStr(FormatUtil.formatBytes(total));
                fileSystemInfo.setAvailable(usable);
                fileSystemInfo.setAvailableStr(FormatUtil.formatBytes(usable));
                fileSystemInfo.setUse(use);
                fileSystemInfo.setUseStr(FormatUtil.formatBytes(use));
                BigDecimal diskbg = new BigDecimal(use*100/total);
                fileSystemInfo.setUsePercent(diskbg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                fileSystemInfos.add(fileSystemInfo);
                diskAvailableTotal += usable;
                diskTotal += total;
            }
            long diskUse = diskTotal - diskAvailableTotal;
            systemResource.setDiskTotal(diskTotal);
            systemResource.setDiskTotalStr(FormatUtil.formatBytes(diskTotal));
            systemResource.setDiskAvailable(diskAvailableTotal);
            systemResource.setDiskAvailableStr(FormatUtil.formatBytes(diskAvailableTotal));
            systemResource.setDiskUse(diskUse);
            systemResource.setDiskUseStr(FormatUtil.formatBytes(diskUse));
            BigDecimal diskBg = new BigDecimal(diskUse*100/diskTotal);
            systemResource.setDiskUsePercent(diskBg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
            systemResource.setFileSystemInfos(fileSystemInfos);

            //获取网卡流量
            long[] afterNetworkRecvAndSents = networkInterfaceRecvAndSent(hal.getNetworkIFs());
            systemResource.setNetworkIn(afterNetworkRecvAndSents[0]-beferNetworkRecvAndSents[0]);
            systemResource.setNetworkOut(afterNetworkRecvAndSents[1]-beferNetworkRecvAndSents[1]);
            Date date = new Date();
            systemResource.setTime(dateToString(date,"HH:mm:ss"));
            systemResource.setDate(dateToString(date,"YYYY-MM-dd"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return systemResource;
    }

    private static long[] networkInterfaceRecvAndSent(NetworkIF[] networkIFs) {
        long recvTotal = 0;
        long sentTotal = 0;
        for (NetworkIF net : networkIFs) {
            recvTotal +=  net.getBytesRecv();
            sentTotal += net.getBytesSent();
        }
        return new long[]{recvTotal,sentTotal};
    }

    public static String dateToString(Date date, String formart) {
        if (date == null) {
            return "";
        } else {
            if (!StringUtils.hasLength(formart.trim())) {
                formart = "yyyy-MM-dd HH:mm:ss";
            }

            SimpleDateFormat sdf = new SimpleDateFormat(formart);
            return sdf.format(date);
        }
    }
}
