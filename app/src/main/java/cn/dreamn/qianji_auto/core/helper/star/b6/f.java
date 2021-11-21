//
// Decompiled by Procyon - 1185ms
//
package cn.dreamn.qianji_auto.core.helper.star.b6;

import cn.dreamn.qianji_auto.bills.BillInfo;


public class f {
    public String a;
    public String b;
    public String c;
    public BillInfo d;
    public double e;
    public boolean f;
    public boolean g;

    public f() {
        this.e = 0.0;
        this.f = false;
        this.g = false;
    }

    @Override
    public String toString() {
        return "ParserModel{account='" + this.a +
                '\'' +
                ", account2='" +
                this.b +
                '\'' +
                ", shop='" +
                this.c +
                '\'' +
                ", entity=" +
                this.d.toString() +
                ", handlingFee=" +
                this.e +
                ", outIncludeHandlingFee=" +
                this.f +
                ", isSamePay=" +
                this.g +
                '}';
    }
}

