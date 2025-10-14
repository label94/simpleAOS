package co.aos.firebase

/** Firebase FireStore 관련 키 */
object FirebaseFireStoreKey {

    /** Users 컬렉션의 Key 정의 */
    enum class UsersCollectionKey(val key: String) {
        UID("uid"),
        ID("id"),
        NICK_NAME("nickName"),
        CREATED_AT("createdAt"),
        UPDATED_AT("updatedAt"),
        LAST_LOGIN_AT("lastLoginAt"),
        LOCAL_PROFILE_IMG_CODE("localProfileImgCode")
    }
}

enum class FirebaseCollection(val value: String) {
    /** 사용자 관리를 위한 users 컬렉션 */
    USER_COLLECTION("users"),

    /** 사용자 id 관리를 위한 emails 컬렉션 */
    USER_EMAILS_COLLECTION("emails"),

    /** 사용자 닉네임 관리를 위한 usernames 컬렉션 */
    USER_NAMES_COLLECTION("usernames")
}