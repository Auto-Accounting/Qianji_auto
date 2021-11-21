//
// Decompiled by Procyon - 1930ms
//
package cn.dreamn.qianji_auto.core.helper.star.b6;


public class b implements Runnable {
    public final /* synthetic */ f a;
    public final /* synthetic */ boolean b;

    public b(final f a, final boolean b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void run() {
       /* final f a = this.a;
        c.f(a.a, (AccountEntity)a.d.account.a());
        final f a2 = this.a;
        c.f(a2.b, (AccountEntity)a2.d.account2.a());
        final f a3 = this.a;
        final String c = a3.c;
        final DetailEntity d = a3.d;
        final boolean b = this.b;
        if (!TextUtils.isEmpty((CharSequence)c)) {
            final DetailEntity detailEntity = new DetailEntity();
            detailEntity.detailType = d.detailType;
            detailEntity.time = d.time;
            b6.c.j(c, detailEntity, b);
            final ToOne itemType = detailEntity.itemType;
            if (itemType != null && itemType.a() != null && (((ItemTypeEntity)detailEntity.itemType.a()).id == ((ItemTypeEntity)d.itemType.a()).id || (c7.b.d(((ItemTypeEntity)detailEntity.itemType.a()).name) && c7.b.d(((ItemTypeEntity)d.itemType.a()).name)))) {
                k.c("[auto] no need saveTypeMap");
            }
            else {
                final QueryBuilder j = e5.j.g((Class)AutoTypeMapEntity.class).j();
                j.B(e.d, c);
                final Query d2 = j.d();
                AutoTypeMapEntity autoTypeMapEntity = (AutoTypeMapEntity)d2.B();
                d2.close();
                if (autoTypeMapEntity == null) {
                    autoTypeMapEntity = new AutoTypeMapEntity(c, (ItemTypeEntity)d.itemType.a());
                }
                else {
                    autoTypeMapEntity.itemType.i((Object)d.itemType.a());
                }
                k.a("CXINCX-Log", "[auto] saveTypeMap");
                e5.j.g((Class)AutoTypeMapEntity.class).h((Object)autoTypeMapEntity);
                final StringBuilder sb = new StringBuilder();
                sb.append(((TypeEntity)d.type.a()).name);
                sb.append("-");
                sb.append(((ItemTypeEntity)d.itemType.a()).name);
                final String string = sb.toString();
                final String d3 = f5.b.d("at_name");
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(f5.b.f());
                sb2.append("_");
                sb2.append(f5.b.d("atp"));
                final String string2 = sb2.toString();
                final StringBuilder sb3 = new StringBuilder();
                sb3.append(c);
                sb3.append("#");
                sb3.append(string);
                f5.b.b(d3, string2, HelperUtils.subStr(sb3.toString(), 256));
            }
        }
        final f a4 = this.a;
        e5.k.j(a4.d, a4.e, a4.f, true);*/
    }
}

