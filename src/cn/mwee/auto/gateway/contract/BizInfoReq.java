package cn.mwee.auto.gateway.contract;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by huming on 16/6/7.
 */
public class BizInfoReq {

    @NotNull(message="clientId不能为空")
    private Integer clientId;

    /**
     * 支付金额
     * 单位 分
     */
    @NotNull(message="amount不能为空")
    @Min(value=1, message="amount必须大于0")
    private Integer amount;


    /**
     * 商品名称
     *
     */
    @NotNull(message="itemName不能为空")
    @Size(min=1,max=255,message="itemName长度必须在1~255个字符之间")
    private String itemName;


    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
