import SparkMD5 from "spark-md5";
// 5m一个分片
const DEFAULT_SIZE = 5 * 1024 * 1024;

const md5 = (file, chunkSize = DEFAULT_SIZE) => {
  return new Promise((resolve, reject) => {
    const startMs = new Date().getTime();
    let blobSlice =
      File.prototype.slice ||
      File.prototype.mozSlice ||
      File.prototype.webkitSlice;
    let chunks = Math.ceil(file.size / chunkSize);
    let currentChunk = 1;
    let spark = new SparkMD5.ArrayBuffer(); //追加数组缓冲区。
    let fileReader = new FileReader(); //读取文件
    fileReader.onload = function (e) {
      spark.append(e.target.result);
      currentChunk++;
      if (currentChunk <= chunks) {
        loadNext();
      } else {
        const md5 = spark.end(); //完成md5的计算，返回十六进制结果。
        console.log(
          "文件md5计算结束，总耗时：",
          (new Date().getTime() - startMs) / 1000,
          "s",
          " 值为:",
          md5
        );
        resolve(md5);
      }
    };
    fileReader.onerror = function (e) {
      reject(e);
    };

    function loadNext() {
    //   console.log("当前part number：", currentChunk, "总块数：", chunks);
      let start = currentChunk * chunkSize;
      let end = start + chunkSize;
      end > file.size && (end = file.size);
      fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
    }
    loadNext();
  });
};

const splitChunk = (chunkSize = DEFAULT_SIZE) => {
  return function (file) {
    return new Promise((resolve, reject) => {
      if (!file) {
        reject("file is null");
      }
      const totalSize = file.size;
      const chunks = [];
      for (let i = 0; i < totalSize; i += chunkSize) {
        const chunk = file.slice(i, i + chunkSize);
        chunks.push(chunk);
      }
      resolve(chunks);
    });
  };
};

export const useUpload = async (file, chunkSize = DEFAULT_SIZE) => {
  return {
    md5: await md5(file, chunkSize),
    splitChunk: splitChunk(chunkSize),
  };
};

function toIntObj(num = 0) {
  const rel = {};
  const str = num < 0 ? -num + '' : num + '';
  const pos = str.indexOf('.');
  const len = pos > -1 ? str.slice(pos + 1).length : 0;
  rel.num = parseFloat(num.toString().replace(/\./g, '')); //去掉小数点转为整数;
  rel.len = len;
  return rel;
}

//计算过程
export function operate(a, b, op) {
  const d1 = toIntObj(a || 0);
  const d2 = toIntObj(b || 0);
  const maxlen = d1.len > d2.len ? d1.len : d2.len; //小数位的最大值
  const max = Math.pow(10, maxlen);
  const o1 = d1.len < maxlen ? d1.num * Math.pow(10, maxlen - d1.len) : d1.num; //如果小数位小于最大值，则补差
  const o2 = d2.len < maxlen ? d2.num * Math.pow(10, maxlen - d2.len) : d2.num;
  switch (op) {
    case '+':
      return (o1 + o2) / max;
    case '-':
      return (o1 - o2) / max;
    case '*':
      return (o1 * o2) / (max * max);
    case '/':
      return o1 / o2;
  }
}
class Currency {
  constructor(num) {
    this.currentNum = num;
  }
  /**
   * @returns {number}
   */
  get val() {
    return this.currentNum;
  }
  get formatVal() {
    return moneyFormat(this.currentNum);
  }
  add(num) {
    this.currentNum = operate(this.currentNum, num, '+');
    return this;
  }
  sub(num) {
    this.currentNum = operate(this.currentNum, num, '-');
    return this;
  }
  multiply(num) {
    this.currentNum = operate(this.currentNum, num, '*');
    return this;
  }
  divide(num) {
    this.currentNum = operate(this.currentNum, num, '/');
    return this;
  }
}
export function caculate(num) {
  return new Currency(num);
}

export function downloadBlobFile(blob, name) {
  if ('msSaveOrOpenBlob' in navigator) {
    window.navigator.msSaveOrOpenBlob(blob, name);
    return;
  }
  const href = window.URL.createObjectURL(blob);
  downUrlFile(href, name);
  window.URL.revokeObjectURL(href); // 释放内存
}

export function downUrlFile(url, name) {
  const eleLink = document.createElement('a');
  eleLink.download = name;
  eleLink.style.display = 'none';
  eleLink.href = url;
  document.body.appendChild(eleLink);
  eleLink.click();
  document.body.removeChild(eleLink);
}

// 分批次执行函数
export async function executePromisesInSequentialBatches(promiseFactories, batchSize = 5) {
  if (!Array.isArray(promiseFactories) || promiseFactories.length === 0) return [];
  const results = [];
  let errors = [];
  for (let i = 0; i < promiseFactories.length; i += batchSize) {
    const batch = promiseFactories.slice(i, i + batchSize);
    // 使用Promise.allSettled来确保即使某些Promise被拒绝，其他Promise仍然会被执行
    const batchResults = await Promise.allSettled(batch.map(factory => 
      typeof factory === 'function' ? factory() : factory.handle()
    ));
    batchResults.forEach((result, index) => {
      if (result.status === "fulfilled") {
        // 如果有options，可以在这里附加到结果中
        if (typeof batch[index] !== 'function' && batch[index].options) {
          results.push(Object.assign(result.value, batch[index].options));
        } else {
          results.push(result.value);
        }
      } else {
        errors.push({ index: i + index, error: result.reason });
      }
    });
    if (errors.length > 0) break; // 如果你希望在遇到任何错误时停止执行，请取消注释此行。
  }
  if (errors.length > 0) {
    throw new Error('上传失败:', errors);
  }
  return results;
}