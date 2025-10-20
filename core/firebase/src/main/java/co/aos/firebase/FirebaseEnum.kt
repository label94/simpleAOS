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

    /** DiaryEntries 컬렉션의 Key 정의 */
    enum class DiaryEntriesCollectionKey(val key: String) {
        D_TITLE("title"),
        D_BODY("body"),
        D_MOOD("mood"),
        D_TAGS("tags"),
        D_PINNED("pinned"),
        D_DATE("date"),
        D_UPDATED_AT("updatedAt")
    }
}

enum class FirebaseCollection(val value: String) {
    /** 사용자 관리를 위한 users 컬렉션 */
    USER_COLLECTION("users"),

    /** 사용자 id 관리를 위한 emails 컬렉션 */
    USER_EMAILS_COLLECTION("emails"),

    /** 사용자 닉네임 관리를 위한 usernames 컬렉션 */
    USER_NAMES_COLLECTION("usernames"),

    /** 다이어리 컨텐츠 관리를 위한 diaryEntries 컬렉션 */
    USER_DIARY_ENTRIES_COLLECTION("diaryEntries"),

    /** 무드 컨텐츠 관리를 위한 moodDaily 컬렉션 */
    USER_MOOD_DAILY_COLLECTION("moodDaily"),
}