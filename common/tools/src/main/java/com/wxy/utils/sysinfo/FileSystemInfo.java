package com.wxy.utils.sysinfo;


import java.io.Serializable;

public class FileSystemInfo implements Serializable {
	private String name;
	private long total;
	private String totalStr;
	private long available;
	private String availableStr;
	private long use;
	private String useStr;
	private double usePercent;

	public FileSystemInfo() {
	}

	public String getName() {
		return this.name;
	}

	public long getTotal() {
		return this.total;
	}

	public String getTotalStr() {
		return this.totalStr;
	}

	public long getAvailable() {
		return this.available;
	}

	public String getAvailableStr() {
		return this.availableStr;
	}

	public long getUse() {
		return this.use;
	}

	public String getUseStr() {
		return this.useStr;
	}

	public double getUsePercent() {
		return this.usePercent;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public void setTotalStr(String totalStr) {
		this.totalStr = totalStr;
	}

	public void setAvailable(long available) {
		this.available = available;
	}

	public void setAvailableStr(String availableStr) {
		this.availableStr = availableStr;
	}

	public void setUse(long use) {
		this.use = use;
	}

	public void setUseStr(String useStr) {
		this.useStr = useStr;
	}

	public void setUsePercent(double usePercent) {
		this.usePercent = usePercent;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof FileSystemInfo)) {
			return false;
		} else {
			FileSystemInfo other = (FileSystemInfo)o;
			if (!other.canEqual(this)) {
				return false;
			} else {
				label75: {
					Object this$name = this.getName();
					Object other$name = other.getName();
					if (this$name == null) {
						if (other$name == null) {
							break label75;
						}
					} else if (this$name.equals(other$name)) {
						break label75;
					}

					return false;
				}

				if (this.getTotal() != other.getTotal()) {
					return false;
				} else {
					Object this$totalStr = this.getTotalStr();
					Object other$totalStr = other.getTotalStr();
					if (this$totalStr == null) {
						if (other$totalStr != null) {
							return false;
						}
					} else if (!this$totalStr.equals(other$totalStr)) {
						return false;
					}

					if (this.getAvailable() != other.getAvailable()) {
						return false;
					} else {
						label59: {
							Object this$availableStr = this.getAvailableStr();
							Object other$availableStr = other.getAvailableStr();
							if (this$availableStr == null) {
								if (other$availableStr == null) {
									break label59;
								}
							} else if (this$availableStr.equals(other$availableStr)) {
								break label59;
							}

							return false;
						}

						if (this.getUse() != other.getUse()) {
							return false;
						} else {
							Object this$useStr = this.getUseStr();
							Object other$useStr = other.getUseStr();
							if (this$useStr == null) {
								if (other$useStr != null) {
									return false;
								}
							} else if (!this$useStr.equals(other$useStr)) {
								return false;
							}

							if (Double.compare(this.getUsePercent(), other.getUsePercent()) != 0) {
								return false;
							} else {
								return true;
							}
						}
					}
				}
			}
		}
	}

	protected boolean canEqual(Object other) {
		return other instanceof FileSystemInfo;
	}

	@Override
	public int hashCode() {
		boolean PRIME = true;
		int result = 1;
		Object $name = this.getName();
		result = result * 59 + ($name == null ? 43 : $name.hashCode());
		long $total = this.getTotal();
		result = result * 59 + (int)($total >>> 32 ^ $total);
		Object $totalStr = this.getTotalStr();
		result = result * 59 + ($totalStr == null ? 43 : $totalStr.hashCode());
		long $available = this.getAvailable();
		result = result * 59 + (int)($available >>> 32 ^ $available);
		Object $availableStr = this.getAvailableStr();
		result = result * 59 + ($availableStr == null ? 43 : $availableStr.hashCode());
		long $use = this.getUse();
		result = result * 59 + (int)($use >>> 32 ^ $use);
		Object $useStr = this.getUseStr();
		result = result * 59 + ($useStr == null ? 43 : $useStr.hashCode());
		long $usePercent = Double.doubleToLongBits(this.getUsePercent());
		result = result * 59 + (int)($usePercent >>> 32 ^ $usePercent);
		return result;
	}

	@Override
	public String toString() {
		return "FileSystemInfo(name=" + this.getName() + ", total=" + this.getTotal() + ", totalStr=" + this.getTotalStr() + ", available=" + this.getAvailable() + ", availableStr=" + this.getAvailableStr() + ", use=" + this.getUse() + ", useStr=" + this.getUseStr() + ", usePercent=" + this.getUsePercent() + ")";
	}
}
