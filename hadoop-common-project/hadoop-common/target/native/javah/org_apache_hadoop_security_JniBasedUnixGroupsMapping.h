/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_apache_hadoop_security_JniBasedUnixGroupsMapping */

#ifndef _Included_org_apache_hadoop_security_JniBasedUnixGroupsMapping
#define _Included_org_apache_hadoop_security_JniBasedUnixGroupsMapping
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_apache_hadoop_security_JniBasedUnixGroupsMapping
 * Method:    anchorNative
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_apache_hadoop_security_JniBasedUnixGroupsMapping_anchorNative
  (JNIEnv *, jclass);

/*
 * Class:     org_apache_hadoop_security_JniBasedUnixGroupsMapping
 * Method:    getGroupsForUser
 * Signature: (Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_org_apache_hadoop_security_JniBasedUnixGroupsMapping_getGroupsForUser
  (JNIEnv *, jclass, jstring);

#ifdef __cplusplus
}
#endif
#endif
