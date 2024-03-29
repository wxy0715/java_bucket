/*
 * Copyright (C) 1998-2014 Christian Schmidt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.util.StringTokenizer;
import java.net.InetAddress;

/**
 * <p>This class represents an Internet Protocol (IP) subnet.</p>
 *
 * @see     java.net.InetAddress
 */

public class Subnet {
    private byte[] addr;
    private byte[] mask;

    /**
     * Creates a <code>Subnet</code> object from the specified
     * address and subnet mask.
     *
     * @param inetAddr the address
     * @param mask the subnet mask represented as a byte array
     *        in network byte order
     */
    public Subnet(InetAddress inetAddr, byte[] mask) {
        this.addr = inetAddr.getAddress();
        this.mask = mask;

        for (int i = 0; i < 4; i++) {
            addr[i] = (byte) (addr[i] & mask[i]);
        }
     }

    /**
     * Creates a <code>Subnet</code> object from the specified
     * address and subnet mask.
     *
     * @param inetAddr the address
     * @param d the subnet mask represented as the number of significant
     *               bits (0 through 32)
     */
    public Subnet(InetAddress inetAddr, short d) {
            if (d > 32) {
                throw new IllegalArgumentException("Invalid format: " + d);
            }

        this.addr = inetAddr.getAddress();

        int m = (0xFFFFFFFF << d) & 0xFFFFFFFF;

        this.mask = new byte[4];
        for (int i = 0; i < 4; i++) {
            mask[i] = (byte) ((m >> 8 * (3 - i)) & 0xFF);
            addr[i] = (byte) (addr[i] & mask[i]);
        }
     }

    /**
     * Creates a <code>Subnet</code> object from the string representation
     * of a subnet mask. The subnet mask may be represented as a single
     * IP-address (<code>123.45.67.89</code>) or as an entire subnet
     * written as a network number and a mask. The mask may either be
     * represented as <code>byte</code> array in network byte order
     * (<code>123.45.67.89/255.255.254.0</code>) or as a number indicating
     * the number of significant bits (<code>123.45.67.89/23</code>).
     *
     */
    public Subnet(String subnet) throws IllegalArgumentException {
            try {
            int j = subnet.indexOf('/');

            if (j == -1) {
                // The format is "123.45.67.89".

                this.addr = parseString(subnet);
                this.mask = new byte[4];
                mask[0] = mask[1] = mask[2] = mask[3] = (byte) 0xFF;
            } else {
                this.addr = parseString(subnet.substring(0, j));

                // The part after the slash.
                String s = subnet.substring(j + 1);
                if (s.indexOf('.') == -1) {
                    // The format is "123.45.67.89/23".

                    short d = Short.parseShort(s);

                    int m = (0xFFFFFFFF << (32 - d)) & 0xFFFFFFFF;

                    this.mask = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        mask[i] = (byte) ((m >> 8 * (3 - i)) & 0xFF);
                        addr[i] = (byte) (addr[i] & mask[i]);
                    }
                } else {
                    // The format is "123.45.67.89/255.255.254.0".

                    this.mask = parseString(s);
                    for (int i = 0; i < 4; i++) {
                        addr[i] = (byte) (addr[i] & mask[i]);
                    }
                }
            }
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid format: " + subnet);
        }
    }

    /**
     * Determines whether an <code>InetAddress</code> is included in
     * a given subnet.
     */
    public boolean isInSubnet(InetAddress inetAddr) {
        byte[] address = inetAddr.getAddress();

        for (int i = 0; i < 4; i++) {
            if (addr[i] != (byte) (address[i] & mask[i])) {
                    return false;
            }
        }
        return true;
    }

    /**
     * Constructs a string representation this subnet. The string is
     * formatted like this: <code>123.45.67.89/255.255.254.0</code>.
     */
    @Override
    public String toString() {
            return (addr[0] & 0xFF) + "." + (addr[1] & 0xFF) + "." +
                   (addr[2] & 0xFF) + "." + (addr[3] & 0xFF) + "/" +
                   (mask[0] & 0xFF) + "." + (mask[1] & 0xFF) + "." +
                   (mask[2] & 0xFF) + "." + (mask[3] & 0xFF);
    }

    private static byte[] parseString(String s) {
        StringTokenizer st = new StringTokenizer(s, ".");

        byte[] addr = new byte[4];
        for (int i = 0; i < 4; i++) {
            addr[i] = (byte) Integer.parseInt(st.nextToken());
        }
        return addr;
    }
}
