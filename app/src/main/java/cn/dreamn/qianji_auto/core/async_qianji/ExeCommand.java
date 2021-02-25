/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.dreamn.qianji_auto.core.async_qianji;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.Runtime.getRuntime;

/**
 * 执行命令的类
 * Created by Kappa
 */
public class ExeCommand {
    //shell进程
    private Process process;
    //对应进程的3个流
    private BufferedReader successResult;
    private BufferedReader errorResult;
    private DataOutputStream os;
    //是否同步，true：run会一直阻塞至完成或超时。false：run会立刻返回
    private boolean bSynchronous;
    //表示shell进程是否还在运行
    private boolean bRunning = false;
    //同步锁
    ReadWriteLock lock = new ReentrantReadWriteLock();

    //保存执行结果
    private StringBuffer result = new StringBuffer();

    /**
     * 构造函数
     *
     * @param synchronous true：同步，false：异步
     */
    public ExeCommand(boolean synchronous) {
        bSynchronous = synchronous;
    }

    /**
     * 默认构造函数，默认是同步执行
     */
    public ExeCommand() {
        bSynchronous = true;
    }

    /**
     * 还没开始执行，和已经执行完成 这两种情况都返回false
     *
     * @return 是否正在执行
     */
    public boolean isRunning() {
        return bRunning;
    }

    /**
     * @return 返回执行结果
     */
    public String getResult() {
        Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return new String(result);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 执行命令
     *
     * @param command eg: cat /sdcard/test.txt
     * @param maxTime 最大等待时间 (ms)
     * @return this
     */
    public ExeCommand run(String[] command, final int maxTime) {
        Log.i("auto", "run command:" + Arrays.toString(command) + ",maxtime:" + maxTime);
        if (command == null || command.length == 0) {
            return this;
        }

        try {
            process = getRuntime().exec("sh");
        } catch (Exception e) {
            return this;
        }
        bRunning = true;
        successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
        errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        os = new DataOutputStream(process.getOutputStream());

        try {
            for (String s : command) {
                os.write(s.getBytes());
                os.writeBytes("\n");
                os.flush();
            }
            //向sh写入要执行的命令


            os.writeBytes("exit\n");
            os.flush();

            os.close();
            //如果等待时间设置为非正，就不开启超时关闭功能
            if (maxTime > 0) {
                //超时就关闭进程
                new Thread(() -> {
                    try {
                        Thread.sleep(maxTime);
                    } catch (Exception e) {
                    }
                    try {
                        int ret = process.exitValue();
                    } catch (IllegalThreadStateException e) {
                        Log.i("auto", "take maxTime,forced to destroy process");
                        process.destroy();
                    }
                }).start();
            }

            //开一个线程来处理input流
            final Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    String line;
                    Lock writeLock = lock.writeLock();
                    try {
                        while ((line = successResult.readLine()) != null) {
                            line += "\n";
                            writeLock.lock();
                            result.append(line);
                            writeLock.unlock();
                        }
                    } catch (Exception e) {
                        Log.i("auto", "read InputStream exception:" + e.toString());
                    } finally {
                        try {
                            successResult.close();
                        } catch (Exception e) {
                            Log.i("auto", "close InputStream exception:" + e.toString());
                        }
                    }
                }
            });
            t1.start();

            //开一个线程来处理error流
            final Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    String line;
                    Lock writeLock = lock.writeLock();
                    try {
                        while ((line = errorResult.readLine()) != null) {
                            line += "\n";
                            writeLock.lock();
                            result.append(line);
                            writeLock.unlock();
                        }
                    } catch (Exception e) {
                        Log.i("auto", "read ErrorStream exception:" + e.toString());
                    } finally {
                        try {
                            errorResult.close();
                        } catch (Exception e) {
                            Log.i("auto", "read ErrorStream exception:" + e.toString());
                        }
                    }
                }
            });
            t2.start();

            Thread t3 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //等待执行完毕
                        t1.join();
                        t2.join();
                        process.waitFor();
                    } catch (Exception e) {

                    } finally {
                        bRunning = false;
                        Log.i("auto", "run command process end");
                    }
                }
            });
            t3.start();

            if (bSynchronous) {
                t3.join();
            }
        } catch (Exception e) {
            Log.i("auto", "run command process exception:" + e.toString());
        }
        return this;
    }

}
