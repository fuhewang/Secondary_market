package com.wangfuhe.wfh.second_shop.permission;

/**Ȩ�޼����� 
* @class PermissionListener 
* @author 
* @date 2016-3-28-����2:42:05 
*/
public interface PermissionListener {
    /**
     * �û���Ȩ�����
     */
    public void  onGranted();
    
    /**
     * �û���ֹ�����
     */
    public void  onDenied();
    
    /**�Ƿ���ʾ������˵��
     * @param permissions ������Ҫ��ʾ˵����Ȩ������
     */
    public void onShowRationale(String[] permissions);
}

