/**
 * 身分證字號驗證工具
 * 包含驗證、格式化和錯誤處理功能
 */
const idValidator = {
    // 英文字母對應的數字
    letterMap: {
        'A': '10', 'B': '11', 'C': '12', 'D': '13', 'E': '14',
        'F': '15', 'G': '16', 'H': '17', 'I': '34', 'J': '18',
        'K': '19', 'L': '20', 'M': '21', 'N': '22', 'O': '35',
        'P': '23', 'Q': '24', 'R': '25', 'S': '26', 'T': '27',
        'U': '28', 'V': '29', 'W': '32', 'X': '30', 'Y': '31',
        'Z': '33'
    },

    /**
     * 驗證身分證字號格式
     * @param {string} id - 身分證字號
     * @returns {object} - 驗證結果和錯誤訊息
     */
    validate(id) {
        const result = {
            isValid: false,
            error: null
        };

        try {
            // 基本格式檢查
            if (!id) {
                throw new Error('身分證字號不能為空');
            }

            // 轉換為大寫並移除空白
            id = id.toUpperCase().trim();

            // 長度檢查
            if (id.length !== 10) {
                throw new Error('身分證字號長度必須為10碼');
            }

            // 第一碼英文字母檢查
            const firstLetter = id.charAt(0);
            if (!this.letterMap[firstLetter]) {
                throw new Error('身分證字號第一碼必須為英文字母');
            }

            // 第二碼性別檢查
            const gender = id.charAt(1);
            if (gender !== '1' && gender !== '2') {
                throw new Error('身分證字號第二碼必須為1或2');
            }

            // 後8碼數字檢查
            const numbers = id.substring(2);
            if (!/^\d{8}$/.test(numbers)) {
                throw new Error('身分證字號後8碼必須為數字');
            }

            // 驗證檢查碼
            if (!this.verifyChecksum(id)) {
                throw new Error('身分證字號格式錯誤');
            }

            result.isValid = true;

        } catch (error) {
            result.error = error.message;
        }

        return result;
    },

    /**
     * 驗證檢查碼
     * @param {string} id - 身分證字號
     * @returns {boolean} - 是否通過驗證
     */
    verifyChecksum(id) {
        try {
            const letter = id.charAt(0);
            const letterNumber = this.letterMap[letter];
            
            // 計算檢查碼
            let sum = parseInt(letterNumber[0]) + parseInt(letterNumber[1]) * 9;
            
            for (let i = 1; i < 9; i++) {
                sum += parseInt(id.charAt(i)) * (9 - i);
            }
            
            sum =sum+ parseInt(id.charAt(9));
            
            return sum % 10 === 0;
        } catch (error) {
            console.error('檢查碼驗證失敗:', error);
            return false;
        }
    },

    /**
     * 格式化顯示（遮罩處理）
     * @param {string} id - 身分證字號
     * @returns {string} - 格式化後的身分證字號
     */
    format(id) {
        if (!id) return '';
        
        try {
            // 確保長度正確
            if (id.length !== 10) {
                return id;
            }
            
            // 只顯示第一個字母和最後四碼
            // return `${id.charAt(0)}***${id.slice(6)}`;
            return id;
        } catch (error) {
            console.error('格式化身分證字號失敗:', error);
            return id;
        }
    },

    /**
     * 取得完整的驗證錯誤說明
     * @param {string} id - 身分證字號
     * @returns {object} - 包含詳細驗證結果的物件
     */
    getValidationDetails(id) {
        const result = this.validate(id);
        return {
            id: id,
            isValid: result.isValid,
            error: result.error,
            formattedId: result.isValid ? this.format(id) : null
        };
    }
};

// 匯出模組
if (typeof module !== 'undefined' && module.exports) {
    module.exports = idValidator;
}