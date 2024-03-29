package com.wxy.utils.sysinfo;

import java.io.Serializable;
import java.util.List;

public class SystemResource implements Serializable {
	private String ip;
	private String productName;
	private String serialNumber;
	private String sysInfo;
	private String upTime;
	private int cpuCores;
	private double cpuUsePercent;
	private long memoryTotal;
	private String memoryTotalStr;
	private long memoryAvailable;
	private String memoryAvailableStr;
	private long memoryUse;
	private String memoryUseStr;
	private double memoryUsePercent;
	private long diskTotal;
	private String diskTotalStr;
	private long diskAvailable;
	private String diskAvailableStr;
	private long diskUse;
	private String diskUseStr;
	private double diskUsePercent;
	private long diskRead;
	private long diskWrite;
	private long networkIn;
	private long networkOut;
	private List<FileSystemInfo> fileSystemInfos;
	private String time;
	private String date;

	public SystemResource() {
	}

	public String getIp() {
		return this.ip;
	}

	public String getProductName() {
		return this.productName;
	}

	public String getSerialNumber() {
		return this.serialNumber;
	}

	public String getSysInfo() {
		return this.sysInfo;
	}

	public String getUpTime() {
		return this.upTime;
	}

	public int getCpuCores() {
		return this.cpuCores;
	}

	public double getCpuUsePercent() {
		return this.cpuUsePercent;
	}

	public long getMemoryTotal() {
		return this.memoryTotal;
	}

	public String getMemoryTotalStr() {
		return this.memoryTotalStr;
	}

	public long getMemoryAvailable() {
		return this.memoryAvailable;
	}

	public String getMemoryAvailableStr() {
		return this.memoryAvailableStr;
	}

	public long getMemoryUse() {
		return this.memoryUse;
	}

	public String getMemoryUseStr() {
		return this.memoryUseStr;
	}

	public double getMemoryUsePercent() {
		return this.memoryUsePercent;
	}

	public long getDiskTotal() {
		return this.diskTotal;
	}

	public String getDiskTotalStr() {
		return this.diskTotalStr;
	}

	public long getDiskAvailable() {
		return this.diskAvailable;
	}

	public String getDiskAvailableStr() {
		return this.diskAvailableStr;
	}

	public long getDiskUse() {
		return this.diskUse;
	}

	public String getDiskUseStr() {
		return this.diskUseStr;
	}

	public double getDiskUsePercent() {
		return this.diskUsePercent;
	}

	public long getDiskRead() {
		return this.diskRead;
	}

	public long getDiskWrite() {
		return this.diskWrite;
	}

	public long getNetworkIn() {
		return this.networkIn;
	}

	public long getNetworkOut() {
		return this.networkOut;
	}

	public List<FileSystemInfo> getFileSystemInfos() {
		return this.fileSystemInfos;
	}

	public String getTime() {
		return this.time;
	}

	public String getDate() {
		return this.date;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setSysInfo(String sysInfo) {
		this.sysInfo = sysInfo;
	}

	public void setUpTime(String upTime) {
		this.upTime = upTime;
	}

	public void setCpuCores(int cpuCores) {
		this.cpuCores = cpuCores;
	}

	public void setCpuUsePercent(double cpuUsePercent) {
		this.cpuUsePercent = cpuUsePercent;
	}

	public void setMemoryTotal(long memoryTotal) {
		this.memoryTotal = memoryTotal;
	}

	public void setMemoryTotalStr(String memoryTotalStr) {
		this.memoryTotalStr = memoryTotalStr;
	}

	public void setMemoryAvailable(long memoryAvailable) {
		this.memoryAvailable = memoryAvailable;
	}

	public void setMemoryAvailableStr(String memoryAvailableStr) {
		this.memoryAvailableStr = memoryAvailableStr;
	}

	public void setMemoryUse(long memoryUse) {
		this.memoryUse = memoryUse;
	}

	public void setMemoryUseStr(String memoryUseStr) {
		this.memoryUseStr = memoryUseStr;
	}

	public void setMemoryUsePercent(double memoryUsePercent) {
		this.memoryUsePercent = memoryUsePercent;
	}

	public void setDiskTotal(long diskTotal) {
		this.diskTotal = diskTotal;
	}

	public void setDiskTotalStr(String diskTotalStr) {
		this.diskTotalStr = diskTotalStr;
	}

	public void setDiskAvailable(long diskAvailable) {
		this.diskAvailable = diskAvailable;
	}

	public void setDiskAvailableStr(String diskAvailableStr) {
		this.diskAvailableStr = diskAvailableStr;
	}

	public void setDiskUse(long diskUse) {
		this.diskUse = diskUse;
	}

	public void setDiskUseStr(String diskUseStr) {
		this.diskUseStr = diskUseStr;
	}

	public void setDiskUsePercent(double diskUsePercent) {
		this.diskUsePercent = diskUsePercent;
	}

	public void setDiskRead(long diskRead) {
		this.diskRead = diskRead;
	}

	public void setDiskWrite(long diskWrite) {
		this.diskWrite = diskWrite;
	}

	public void setNetworkIn(long networkIn) {
		this.networkIn = networkIn;
	}

	public void setNetworkOut(long networkOut) {
		this.networkOut = networkOut;
	}

	public void setFileSystemInfos(List<FileSystemInfo> fileSystemInfos) {
		this.fileSystemInfos = fileSystemInfos;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof SystemResource)) {
			return false;
		} else {
			SystemResource other = (SystemResource)o;
			if (!other.canEqual(this)) {
				return false;
			} else {
				label235: {
					Object this$ip = this.getIp();
					Object other$ip = other.getIp();
					if (this$ip == null) {
						if (other$ip == null) {
							break label235;
						}
					} else if (this$ip.equals(other$ip)) {
						break label235;
					}

					return false;
				}

				Object this$productName = this.getProductName();
				Object other$productName = other.getProductName();
				if (this$productName == null) {
					if (other$productName != null) {
						return false;
					}
				} else if (!this$productName.equals(other$productName)) {
					return false;
				}

				Object this$serialNumber = this.getSerialNumber();
				Object other$serialNumber = other.getSerialNumber();
				if (this$serialNumber == null) {
					if (other$serialNumber != null) {
						return false;
					}
				} else if (!this$serialNumber.equals(other$serialNumber)) {
					return false;
				}

				label214: {
					Object this$sysInfo = this.getSysInfo();
					Object other$sysInfo = other.getSysInfo();
					if (this$sysInfo == null) {
						if (other$sysInfo == null) {
							break label214;
						}
					} else if (this$sysInfo.equals(other$sysInfo)) {
						break label214;
					}

					return false;
				}

				label207: {
					Object this$upTime = this.getUpTime();
					Object other$upTime = other.getUpTime();
					if (this$upTime == null) {
						if (other$upTime == null) {
							break label207;
						}
					} else if (this$upTime.equals(other$upTime)) {
						break label207;
					}

					return false;
				}

				if (this.getCpuCores() != other.getCpuCores()) {
					return false;
				} else if (Double.compare(this.getCpuUsePercent(), other.getCpuUsePercent()) != 0) {
					return false;
				} else if (this.getMemoryTotal() != other.getMemoryTotal()) {
					return false;
				} else {
					Object this$memoryTotalStr = this.getMemoryTotalStr();
					Object other$memoryTotalStr = other.getMemoryTotalStr();
					if (this$memoryTotalStr == null) {
						if (other$memoryTotalStr != null) {
							return false;
						}
					} else if (!this$memoryTotalStr.equals(other$memoryTotalStr)) {
						return false;
					}

					if (this.getMemoryAvailable() != other.getMemoryAvailable()) {
						return false;
					} else {
						Object this$memoryAvailableStr = this.getMemoryAvailableStr();
						Object other$memoryAvailableStr = other.getMemoryAvailableStr();
						if (this$memoryAvailableStr == null) {
							if (other$memoryAvailableStr != null) {
								return false;
							}
						} else if (!this$memoryAvailableStr.equals(other$memoryAvailableStr)) {
							return false;
						}

						if (this.getMemoryUse() != other.getMemoryUse()) {
							return false;
						} else {
							Object this$memoryUseStr = this.getMemoryUseStr();
							Object other$memoryUseStr = other.getMemoryUseStr();
							if (this$memoryUseStr == null) {
								if (other$memoryUseStr != null) {
									return false;
								}
							} else if (!this$memoryUseStr.equals(other$memoryUseStr)) {
								return false;
							}

							if (Double.compare(this.getMemoryUsePercent(), other.getMemoryUsePercent()) != 0) {
								return false;
							} else if (this.getDiskTotal() != other.getDiskTotal()) {
								return false;
							} else {
								Object this$diskTotalStr = this.getDiskTotalStr();
								Object other$diskTotalStr = other.getDiskTotalStr();
								if (this$diskTotalStr == null) {
									if (other$diskTotalStr != null) {
										return false;
									}
								} else if (!this$diskTotalStr.equals(other$diskTotalStr)) {
									return false;
								}

								if (this.getDiskAvailable() != other.getDiskAvailable()) {
									return false;
								} else {
									Object this$diskAvailableStr = this.getDiskAvailableStr();
									Object other$diskAvailableStr = other.getDiskAvailableStr();
									if (this$diskAvailableStr == null) {
										if (other$diskAvailableStr != null) {
											return false;
										}
									} else if (!this$diskAvailableStr.equals(other$diskAvailableStr)) {
										return false;
									}

									if (this.getDiskUse() != other.getDiskUse()) {
										return false;
									} else {
										label154: {
											Object this$diskUseStr = this.getDiskUseStr();
											Object other$diskUseStr = other.getDiskUseStr();
											if (this$diskUseStr == null) {
												if (other$diskUseStr == null) {
													break label154;
												}
											} else if (this$diskUseStr.equals(other$diskUseStr)) {
												break label154;
											}

											return false;
										}

										if (Double.compare(this.getDiskUsePercent(), other.getDiskUsePercent()) != 0) {
											return false;
										} else if (this.getDiskRead() != other.getDiskRead()) {
											return false;
										} else if (this.getDiskWrite() != other.getDiskWrite()) {
											return false;
										} else if (this.getNetworkIn() != other.getNetworkIn()) {
											return false;
										} else if (this.getNetworkOut() != other.getNetworkOut()) {
											return false;
										} else {
											label140: {
												Object this$fileSystemInfos = this.getFileSystemInfos();
												Object other$fileSystemInfos = other.getFileSystemInfos();
												if (this$fileSystemInfos == null) {
													if (other$fileSystemInfos == null) {
														break label140;
													}
												} else if (this$fileSystemInfos.equals(other$fileSystemInfos)) {
													break label140;
												}

												return false;
											}

											Object this$time = this.getTime();
											Object other$time = other.getTime();
											if (this$time == null) {
												if (other$time != null) {
													return false;
												}
											} else if (!this$time.equals(other$time)) {
												return false;
											}

											Object this$date = this.getDate();
											Object other$date = other.getDate();
											if (this$date == null) {
												if (other$date != null) {
													return false;
												}
											} else if (!this$date.equals(other$date)) {
												return false;
											}

											return true;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean canEqual(Object other) {
		return other instanceof SystemResource;
	}

	@Override
	public int hashCode() {
		boolean PRIME = true;
		int result = 1;
		Object $ip = this.getIp();
		result = result * 59 + ($ip == null ? 43 : $ip.hashCode());
		Object $productName = this.getProductName();
		result = result * 59 + ($productName == null ? 43 : $productName.hashCode());
		Object $serialNumber = this.getSerialNumber();
		result = result * 59 + ($serialNumber == null ? 43 : $serialNumber.hashCode());
		Object $sysInfo = this.getSysInfo();
		result = result * 59 + ($sysInfo == null ? 43 : $sysInfo.hashCode());
		Object $upTime = this.getUpTime();
		result = result * 59 + ($upTime == null ? 43 : $upTime.hashCode());
		result = result * 59 + this.getCpuCores();
		long $cpuUsePercent = Double.doubleToLongBits(this.getCpuUsePercent());
		result = result * 59 + (int)($cpuUsePercent >>> 32 ^ $cpuUsePercent);
		long $memoryTotal = this.getMemoryTotal();
		result = result * 59 + (int)($memoryTotal >>> 32 ^ $memoryTotal);
		Object $memoryTotalStr = this.getMemoryTotalStr();
		result = result * 59 + ($memoryTotalStr == null ? 43 : $memoryTotalStr.hashCode());
		long $memoryAvailable = this.getMemoryAvailable();
		result = result * 59 + (int)($memoryAvailable >>> 32 ^ $memoryAvailable);
		Object $memoryAvailableStr = this.getMemoryAvailableStr();
		result = result * 59 + ($memoryAvailableStr == null ? 43 : $memoryAvailableStr.hashCode());
		long $memoryUse = this.getMemoryUse();
		result = result * 59 + (int)($memoryUse >>> 32 ^ $memoryUse);
		Object $memoryUseStr = this.getMemoryUseStr();
		result = result * 59 + ($memoryUseStr == null ? 43 : $memoryUseStr.hashCode());
		long $memoryUsePercent = Double.doubleToLongBits(this.getMemoryUsePercent());
		result = result * 59 + (int)($memoryUsePercent >>> 32 ^ $memoryUsePercent);
		long $diskTotal = this.getDiskTotal();
		result = result * 59 + (int)($diskTotal >>> 32 ^ $diskTotal);
		Object $diskTotalStr = this.getDiskTotalStr();
		result = result * 59 + ($diskTotalStr == null ? 43 : $diskTotalStr.hashCode());
		long $diskAvailable = this.getDiskAvailable();
		result = result * 59 + (int)($diskAvailable >>> 32 ^ $diskAvailable);
		Object $diskAvailableStr = this.getDiskAvailableStr();
		result = result * 59 + ($diskAvailableStr == null ? 43 : $diskAvailableStr.hashCode());
		long $diskUse = this.getDiskUse();
		result = result * 59 + (int)($diskUse >>> 32 ^ $diskUse);
		Object $diskUseStr = this.getDiskUseStr();
		result = result * 59 + ($diskUseStr == null ? 43 : $diskUseStr.hashCode());
		long $diskUsePercent = Double.doubleToLongBits(this.getDiskUsePercent());
		result = result * 59 + (int)($diskUsePercent >>> 32 ^ $diskUsePercent);
		long $diskRead = this.getDiskRead();
		result = result * 59 + (int)($diskRead >>> 32 ^ $diskRead);
		long $diskWrite = this.getDiskWrite();
		result = result * 59 + (int)($diskWrite >>> 32 ^ $diskWrite);
		long $networkIn = this.getNetworkIn();
		result = result * 59 + (int)($networkIn >>> 32 ^ $networkIn);
		long $networkOut = this.getNetworkOut();
		result = result * 59 + (int)($networkOut >>> 32 ^ $networkOut);
		Object $fileSystemInfos = this.getFileSystemInfos();
		result = result * 59 + ($fileSystemInfos == null ? 43 : $fileSystemInfos.hashCode());
		Object $time = this.getTime();
		result = result * 59 + ($time == null ? 43 : $time.hashCode());
		Object $date = this.getDate();
		result = result * 59 + ($date == null ? 43 : $date.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "SystemResource(ip=" + this.getIp() + ", productName=" + this.getProductName() + ", serialNumber=" + this.getSerialNumber() + ", sysInfo=" + this.getSysInfo() + ", upTime=" + this.getUpTime() + ", cpuCores=" + this.getCpuCores() + ", cpuUsePercent=" + this.getCpuUsePercent() + ", memoryTotal=" + this.getMemoryTotal() + ", memoryTotalStr=" + this.getMemoryTotalStr() + ", memoryAvailable=" + this.getMemoryAvailable() + ", memoryAvailableStr=" + this.getMemoryAvailableStr() + ", memoryUse=" + this.getMemoryUse() + ", memoryUseStr=" + this.getMemoryUseStr() + ", memoryUsePercent=" + this.getMemoryUsePercent() + ", diskTotal=" + this.getDiskTotal() + ", diskTotalStr=" + this.getDiskTotalStr() + ", diskAvailable=" + this.getDiskAvailable() + ", diskAvailableStr=" + this.getDiskAvailableStr() + ", diskUse=" + this.getDiskUse() + ", diskUseStr=" + this.getDiskUseStr() + ", diskUsePercent=" + this.getDiskUsePercent() + ", diskRead=" + this.getDiskRead() + ", diskWrite=" + this.getDiskWrite() + ", networkIn=" + this.getNetworkIn() + ", networkOut=" + this.getNetworkOut() + ", fileSystemInfos=" + this.getFileSystemInfos() + ", time=" + this.getTime() + ", date=" + this.getDate() + ")";
	}
}
