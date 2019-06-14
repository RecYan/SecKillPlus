package com.recyan.www.seckill.exception;

import com.recyan.www.seckill.result.CodeMsg;

public class GlobalException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4606491464014175572L;
	private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.getMsg());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
