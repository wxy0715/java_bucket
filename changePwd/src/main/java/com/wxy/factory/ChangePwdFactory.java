package com.wxy.factory;

import com.wxy.entity.ChangePwd;
import com.wxy.factory.changePwd.Linux;
import com.wxy.factory.changePwd.Windows;


public class ChangePwdFactory implements IChangePwdFactory{

    @Override
    public IChangePwd createChangePwd(ChangePwd changePwd) {
        IChangePwd iChangePwd;
        switch (changePwd.getDataBaseEnum()) {
            case LINUX:
                iChangePwd = new Linux(changePwd);
                break;
            case WINDOWS:
                iChangePwd = new Windows(changePwd);
                break;
            default:
                iChangePwd = null;
        }
        return iChangePwd;
    }
}
