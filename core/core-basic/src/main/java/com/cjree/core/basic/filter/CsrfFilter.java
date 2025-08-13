package com.cjree.core.basic.filter;

import com.cjree.core.basic.util.WebUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class CsrfFilter implements Filter {

    /**
     * 白名单
     */
    private List<String> whiteUrls;
    private int _size = 0;

    @Override
    public void init(FilterConfig filterConfig) {
        // 读取文件
        String path = CsrfFilter.class.getResource("/").getFile();
        whiteUrls = readFile(path + "csrfWhite.txt");
        _size = null == whiteUrls ? 0 : whiteUrls.size();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            // 获取请求url地址
            String url = req.getRequestURL().toString();
            String referurl = req.getHeader("Referer");
            if (isWhiteReq(referurl)) {
                chain.doFilter(request, response);
            } else {
                req.getRequestDispatcher("/").forward(req, res);

                String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String clientIp = WebUtil.getHost(req);

                // 记录跨站请求日志
                String logContent = "跨站请求---->>>" + clientIp + "||" + date + "||" + referurl + "||" + url;
                log.warn(logContent);
                return;
            }

        } catch (Exception e) {
            log.error("doFilter", e);
        }

    }

    /**
     * 判断是否是白名单
     *
     * @param referUrl
     * @return
     */
    private boolean isWhiteReq(String referUrl) {
        if (referUrl == null || "".equals(referUrl) || _size == 0) {
            return true;
        } else {
            String refHost = "";
            referUrl = referUrl.toLowerCase();
            if (referUrl.startsWith("http://")) {
                refHost = referUrl.substring(7);
            } else if (referUrl.startsWith("https://")) {
                refHost = referUrl.substring(8);
            }

            for (String urlTemp : whiteUrls) {
                if (refHost.indexOf(urlTemp.toLowerCase()) > -1) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void destroy() {
    }

    private List<String> readFile(String fileName) {
        List<String> list = new ArrayList<String>();
        BufferedReader reader = null;
        FileInputStream fis = null;
        try {
            File f = new File(fileName);
            if (f.isFile() && f.exists()) {
                fis = new FileInputStream(f);
                reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!"".equals(line)) {
                        list.add(line);
                    }
                }
            }
        } catch (Exception e) {
            log.error("readFile", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                log.error("InputStream关闭异常", e);
            }
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                log.error("FileInputStream关闭异常", e);
            }
        }
        return list;
    }
}
