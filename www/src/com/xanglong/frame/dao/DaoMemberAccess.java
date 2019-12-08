package com.xanglong.frame.dao;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;

import ognl.MemberAccess;

/**OGNL插件包必须实现该接口*/
public class DaoMemberAccess implements MemberAccess {

	public boolean	allowPrivateAccess = false;
	public boolean	allowProtectedAccess = false;
	public boolean	allowPackageProtectedAccess = false;
 
	public DaoMemberAccess(boolean allowAllAccess) {
		this(allowAllAccess, allowAllAccess, allowAllAccess);
	}

	public DaoMemberAccess(boolean allowPrivateAccess, boolean allowProtectedAccess, boolean allowPackageProtectedAccess) {
		super();
		this.allowPrivateAccess = allowPrivateAccess;
		this.allowProtectedAccess = allowProtectedAccess;
		this.allowPackageProtectedAccess = allowPackageProtectedAccess;
	}

	public boolean getAllowPrivateAccess() {
		return allowPrivateAccess;
	}

	public void setAllowPrivateAccess(boolean value){
		allowPrivateAccess = value;
	}
 
	public boolean getAllowProtectedAccess() {
		return allowProtectedAccess;
	}
 
	public void setAllowProtectedAccess(boolean value){
		allowProtectedAccess = value;
	}
 
	public boolean getAllowPackageProtectedAccess()	{
		return allowPackageProtectedAccess;
	}
 
	public void setAllowPackageProtectedAccess(boolean value) {
		allowPackageProtectedAccess = value;
	}

	public Object setup(@SuppressWarnings("rawtypes") Map context, Object target, Member member, String propertyName) {
	    Object result = null;
	    if (isAccessible(context, target, member, propertyName)) {
	        AccessibleObject accessible = (AccessibleObject) member;
	        if (!accessible.isAccessible()) {
	            result = Boolean.FALSE;
	            accessible.setAccessible(true);
	        }
	    }
	    return result;
	}
 
    public void restore(@SuppressWarnings("rawtypes") Map context, Object target, Member member, String propertyName, Object state) {
		if (state != null) {
		    ((AccessibleObject)member).setAccessible(((Boolean)state).booleanValue());
		}
    }

    public boolean isAccessible(@SuppressWarnings("rawtypes") Map context, Object target, Member member, String propertyName) {
		int modifiers = member.getModifiers();
		boolean result = Modifier.isPublic(modifiers);
		if (!result) {
			if (Modifier.isPrivate(modifiers)) {
				result = getAllowPrivateAccess();
			} else {
				if (Modifier.isProtected(modifiers)) {
					result = getAllowProtectedAccess();
				} else {
					result = getAllowPackageProtectedAccess();
				}
			}
		}
		return result;
	}

}