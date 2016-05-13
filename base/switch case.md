```java
   long canGive = 0;
   switch (currency) {
	// 礼券、声望等，不能超过int最大值
   case GIFT_BOND:
   case HONOR:
   case SKILL_POINT:
        long _value = this.getBaseStrProperties().getLong(currency.getPropIndex());
        canGive = Integer.MAX_VALUE - _value;
        break;

	
```

这样一来的话, GIFT_BOND ,HONOR, SKILL_POINT,三个走的都是SKILL_POINT里面赋值语句,这个要特别注意下