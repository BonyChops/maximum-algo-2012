package jp.dai1741.competitive.util;

import jp.dai1741.competitive.util.Graphs.Edge;
import jp.dai1741.competitive.util.Graphs.ListsGraph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class Geometries2D {

    static final double EPS = 1e-9;

    static boolean approxEquals(double a, double b) {
        return Math.abs(a - b) < EPS;
    }

    static class Point extends Point2D.Double implements Comparable<Point> {

        public Point() {
        }

        public Point(double x, double y) {
            super(x, y);
            // x��y��final�ł͂Ȃ����A���̃��C�u�����֐��̈ꕔ�͕ύX����Ȃ����Ƃ�O��Ƃ��Ă���̂Œ��ӁB
        }

        /** dot(v1,v2)=|v1||v2|cos�� */
        double dot(Point p) {
            return x * p.x + y * p.y;
        }

        /** cross(v1,v2)=|v1||v2|sin��  */
        double cross(Point p) {
            return x * p.y - y * p.x;
        }

        double distanceSqr() {
            return x * x + y * y;
        }

        double distance() {
            return Math.hypot(x, y);
        }

        Point add(Point p) {
            return new Point(x + p.x, y + p.y);
        }

        Point multiply(double k) {
            return new Point(k * x, k * y);
        }

        Point multiply(Point p) {  // complex mul: (x+yi)*(p.x+p.yi)
            return new Point(x * p.x - y * p.y, x * p.y + p.x * y);
        }

        Point subtract(Point begin) {
            return new Point(x - begin.x, y - begin.y);
        }

        @Override
        public boolean equals(Object obj) {  // ���̊֐���Eclipse�Ő������č��W�̔�r��������������΂���
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Point other = (Point) obj;
            if (!approxEquals(x, other.x)) return false;
            if (!approxEquals(y, other.y)) return false;
            return true;
        }

        @Override
        public int compareTo(Point o) {
            if (!approxEquals(x, o.x)) return (int) Math.signum(x - o.x);
            if (!approxEquals(y, o.y)) return (int) Math.signum(y - o.y);
            return 0;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }

    }

    /**
     * @return �x�N�g�� a->b ���猩�ăx�N�g�� b->c ���������Ȃ�1�A�E�����Ȃ�-1
     * @see http://www.prefield.com/algorithm/geometry/ccw.html
     */
    static int ccw(Point a, Point b, Point c) {
        b = b.subtract(a);
        c = c.subtract(a);
        if (b.cross(c) > EPS) return +1;                  // counter clockwise
        if (b.cross(c) + EPS < 0) return -1;              // clockwise
        if (b.dot(c) + EPS < 0) return +2;                // c--a--b on line and a!=c
        if (b.distanceSqr() < c.distanceSqr()) return -2; // a--b--c on line or a==b�@����{�I��a==b�ƂȂ�ׂ��łȂ��@
        return 0;                                         // a--c--b on line or b==c
    }

    /*
     * ��������
     * @see http://www.prefield.com/algorithm/geometry/intersection.html
     */

    /** @return ����a�ƒ���b���������Ă���Ȃ�true */
    static boolean intersectsLL(Point a1, Point a2, Point b1, Point b2) {
        Point a = a2.subtract(a1);
        Point b = b2.subtract(b1);
        return !intersectsLP(a, b, new Point()) || intersectsLP(a1, b1, b2);
    }

    /** @return ����a�Ɛ���b���������Ă���Ȃ�true */
    static boolean intersectsLS(Point a1, Point a2, Point b1, Point b2) {
        // a1�����_�Ɉړ�
        Point a = a2.subtract(a1);
        b1 = b1.subtract(a1);
        b2 = b2.subtract(a1);
        return a.cross(b1) * a.cross(b2) < EPS;  // ����b������a���܂����Ȃ�true
    }

    /** @return ����a�Ɠ_b���������Ă���Ȃ�true */
    static boolean intersectsLP(Point a1, Point a2, Point b) {
        int ccw = ccw(a1, a2, b);
        return ccw != 1 && ccw != -1;
    }

    static boolean intersectsSS(Point a1, Point a2, Point b1, Point b2) {
        // �݂��̒[�_�����g�̍��E�ɕ�����Ă���Ȃ�true
        return ccw(a1, a2, b1) * ccw(a1, a2, b2) <= 0
                && ccw(b1, b2, a1) * ccw(b1, b2, a2) <= 0;
    }

    static boolean intersectsSP(Point a1, Point a2, Point b) {
        return ccw(a1, a2, b) == 0;
    }

    /*
     * ����
     * @see http://www.prefield.com/algorithm/geometry/distance.html
     */

    /** @return ����a�ɓ_p�𓊉e�����Ƃ��̈ʒu */
    static Point projection(Point a1, Point a2, Point p) {
        Point a = a2.subtract(a1);
        p = p.subtract(a1);
        double t = a.dot(p) / a.distanceSqr();
        // |a||p|cos��=t|a|^2, a,t���Œ�ŃƂ������Ƃ��A�_p�̋O�Ղ͒���
        return a1.add(a.multiply(t));
    }

    static Point reflection(Point a1, Point a2, Point p) {
        Point dir = projection(a1, a2, p).subtract(p);
        return p.add(dir.multiply(2));
    }

    static double distanceLP(Point a1, Point a2, Point p) {
        return projection(a1, a2, p).distance(p);
    }

    static double distanceLL(Point a1, Point a2, Point b1, Point b2) {
        if (intersectsLL(a1, a2, b1, b2)) return 0;
        return distanceLP(a1, a2, b1);
    }

    static double distanceLS(Point a1, Point a2, Point b1, Point b2) {
        if (intersectsLS(a1, a2, b1, b2)) return 0;
        return Math.min(distanceLP(a1, a2, b1), distanceLP(a1, a2, b2));
    }

    static double distanceSP(Point a1, Point a2, Point b) {
        Point r = projection(a1, a2, b);
        // ���e�����_��������ɂ���Ȃ�A�_p���炻�̓_�ւ̋�����Ԃ��΂���
        if (intersectsSP(a1, a2, r)) return r.distance(b);
        return Math.min(b.distance(a1), b.distance(a2));
    }

    static double distanceSS(Point a1, Point a2, Point b1, Point b2) {
        if (intersectsSS(a1, a2, b1, b2)) return 0;
        return Math.min(Math.min(b1.distance(a1), b1.distance(a2)), Math.min(b2
                .distance(a1), b2.distance(a2)));
    }

    /** @see http://www.deqnotes.net/acmicpc/2d_geometry/lines#intersection_of_lines */
    static Point crosspointLL(Point a1, Point a2, Point b1, Point b2) {
        // �x�N�g��a�̒�����d1/d2�{����ƒ���b�ɐڂ���悤��d1,d2��ݒ�
        Point a = a2.subtract(a1);
        Point b = b2.subtract(b1);
        double d1 = b.cross(b1.subtract(a1));
        double d2 = b.cross(a);
        if (Math.abs(d1) < EPS && Math.abs(d2) < EPS) return a1;  // same line
        if (Math.abs(d2) < EPS) throw new IllegalArgumentException(
                "PRECONDITION NOT SATISFIED");
        return a1.add(a.multiply(d1 / d2));
    }

    /*
     * �~�̌�������Ƌ�������
     * �T�[�N���̐�y��������������Q�l�ɂ��ď������i�����܂茴�`���Ƃǂ߂Ă��Ȃ��j
     */

    /** @return ����a�Ɖ~b�̋��� */
    static double distanceLC(Point a1, Point a2, Point b, double r) {
        return Math.max(distanceLP(a1, a2, b) - r, 0);
    }

    static double distanceSC(Point a1, Point a2, Point b, double r) {
        double dSqr1 = b.subtract(a1).distanceSqr();
        double dSqr2 = b.subtract(a2).distanceSqr();
        boolean a1InCircle = dSqr1 < r * r;
        boolean a2InCircle = dSqr2 < r * r;
        if (a1InCircle && a2InCircle) {  // �����̗��[���~�̒�
            return r - Math.sqrt(Math.max(dSqr1, dSqr2));
        }
        if (a1InCircle ^ a2InCircle) return 0;  // �[�_���~�̒��ƊO�Ȃ疾�炩�Ɉ�_�Ō����
        // �����̒[�_�����S�ɉ~�̊O�Ȃ�Γ_�Ƃ̋�������ł悢
        return Math.max(distanceSP(a1, a2, b) - r, 0);
    }

    /**
     * @return �����_�̑g�Bret.length �͌����_�̌��ƂȂ�B
     * @see http://homepage1.nifty.com/gfk/circle-line.htm
     */
    static Point[] crosspointLC(Point a1, Point a2, Point b, double r) {
        // ����a�ɉ~�̒��S���琂���̑������炵�A���������̃x�N�g���𓾂āA
        // �O�����̒藝�ő������_�ւ̋��������߂�
        Point foot = projection(a1, a2, b);
        double footLenSqr = foot.distanceSq(b);
        Point dir = a2.subtract(a1);
        if (approxEquals(r * r, footLenSqr)) {  // ��_�Őڂ���ꍇ�i�덷������̂��ߕ���j
            return new Point[] { foot };
        }
        if (r * r < footLenSqr) return new Point[0];

        double len = Math.sqrt(r * r - footLenSqr) / dir.distance();
        dir = dir.multiply(len);
        return new Point[] { foot.add(dir), foot.subtract(dir), };
    }

    /** @return 2�~�̊֌W��\�����l�B�l�͓K���B���̊֐�����Ȃ��̂ł́B */
    static int intersectionCC(Point a, double ar, Point b, double br) {
        double dSqr = a.distanceSq(b);
        if (approxEquals(dSqr, (ar + br) * (ar + br))) return 2;  // �O�ڂ���
        if (approxEquals(dSqr, (ar - br) * (ar - br))) return 3;  // ���ڂ���
        if (dSqr < (ar - br) * (ar - br)) return -1;  // �Е��̉~�͂����Е��̓���
        if ((ar + br) * (ar + br) < dSqr) return -2;  // �Е��̉~�͂����Е��ƑS�R�֌W���Ȃ�
        return 1;  // 2�_�Ō����
    }

    static double distanceCC(Point a, double ar, Point b, double br) {
        double dSqr = a.distanceSq(b);
        // �Е��̉~�͂����Е��̓���
        if (dSqr + EPS < (ar - br) * (ar - br)) return Math.abs(ar - br)
                - Math.sqrt(dSqr);
        // �Е��̉~�͂����Е��Ɨ���Ă���
        if ((ar + br) * (ar + br) + EPS < dSqr) return Math.sqrt(dSqr) - ar - br;
        return 0;  // �ڂ��邩������Ă���
    }

    /**
     * @return 2�~�̌����_�̔z��B�z��̗v�f���͌����_�̌��ƂȂ�B
     */
    static Point[] crosspointCC(Point a, double ar, Point b, double br) {
        Point ab = b.subtract(a);
        double d = ab.distance();

        // �]���藝�ŉ~�̒��S����2�~�̌�_�����Ԓ����ւ̋��������߁A
        // �O�����̒藝��2�~�̌�_�����Ԓ����̒��������߂�
        double lenToCross = (ab.distanceSqr() + ar * ar - br * br) / (2 * d);
        double lenRef = Math.sqrt(ar * ar - lenToCross * lenToCross);
        if (d < EPS || ar < lenToCross) return new Point[0];  // ��_�Ȃ��i���������݁j

        // ���������_�̈ʒu�����߂�
        Point abN = ab.multiply(new Point(0, 1)).multiply(lenRef / d);
        Point crosspoint = a.add(ab.multiply(lenToCross / d));
        Point[] ret = new Point[] { crosspoint.add(abN), crosspoint.subtract(abN) };
        return ret[0].equals(ret[1]) ? new Point[] { ret[0] } : ret;  // ��_��1�̂Ƃ��������i�s�v�Ȃ�����j
    }

    /**
     * @param b �~�̊O���̓_
     * @return �ړ_2�B��_��1�̂Ƃ��ib���~����j�̂Ƃ���2�v�f�̔z���Ԃ��B
     */
    static Point[] tangent(Point a, double ar, Point b) {
        Point ba = a.subtract(b);
        double baLen = ba.distance();
        if (baLen < ar) return new Point[0];  // �ڐ��Ȃ�

        double cos = Math.sqrt(ba.distanceSqr() - ar * ar) / baLen;
        double sin = ar / baLen;
        Point dir = ba.multiply(new Point(cos, sin));
        Point dir2 = ba.multiply(new Point(cos, -sin));
        return new Point[] { b.add(dir.multiply(cos)), b.add(dir2.multiply(cos)) };
        
//        Point ab = b.subtract(a);
//        double len = ab.distance();
//        if (len < ar) return new Point[0];  // �ڐ��Ȃ�
//        double theta = Math.PI/2 - Math.asin(ar / len);
//        double cos = Math.cos(theta);
//        double sin = Math.sin(theta);
//        Point rot = new Point(cos, -sin);
//        return new Point[] { a.add(ab.multiply(rot.multiply(ar/len))), a.add(ab.multiply(new Point(cos, sin).multiply(ar/len))) };
    }

    /** 2�~�ɑ΂���ڐ��̌�_��Ԃ��B���̊֐�����Ȃ��̂ł́BNaN�Ԃ��Ƃ��Ɉ��B */
    static Point[] bitangentPoints(Point a, double ar, Point b, double br) {
        int type = intersectionCC(a, ar, b, br);
        Point ab = b.subtract(a);
        Point inner = null;
        Point outer = null;
        if (type != 3 && type != -1) inner = a.add(ab.multiply(ar / (ar + br)));
        if (type != 2 && type != -1) outer = ar != br ? a
                .add(ab.multiply(ar / (ar - br))) : new Point(Double.NaN, Double.NaN);
        return new Point[] { outer, inner };
    }

    /**
     * @return 2�~�̋��ʐڐ��̑g�Bret[0]�ɋ��ʊO�ڐ��Aret[1]�ɋ��ʓ��ڐ��B
     *         �Ή�����ڐ����Ȃ��ior�ڐ��������ɂ���j�ꍇ�Aret[i]��null�B
     *         ret[i][j]�Ɋe�ڐ����i�[�����Bret[i][j][0]�ɉ~a�̐ړ_�Aret[i][j][1]�ɉ~b�̐ړ_�B
     *         ret[i][0][0]�͐���AB���猩�č����ɁAret[i][1][0]�͐���AB���猩�ĉE���ɂ���B
     *         �ڐ����d�Ȃ�ꍇ�Aret[i][1]��null�B�O�ڐ��Ɠ��ڐ�����v����ꍇ�͏d�����ė񋓂���B
     * @see https://github.com/infnty/acm/blob/master/lib/geometry/CircleTangents.java
     */
    static Point[][][] tangentLines(Point a, double ar, Point b, double br) {
        Point[][][] ret = new Point[2][][];
        double d = a.distance(b);
        Point v = b.subtract(a).multiply(1 / d);  // a����b�ւ̒P�ʃx�N�g��

        for (int sign = +1, i = 0; sign >= -1; sign -= 2, i++) {  // �O�ڐ� -> ���ڐ�
            double sin = (ar - sign * br) / d;  // �x�N�g��ab���ΕӁA���a�̘a/���͐����i�����t���j
            if (sin * sin > 1 + EPS || sin != sin) break;  // �����������a�̘a/���̕����傫�����\�Ȑڐ��𒲂ׂ�����

            ret[i] = new Point[2][];
            double cos = Math.sqrt(Math.max(1 - sin * sin, 0));

            for (int j = 0; j < 2; j++) {  // 2�̐ڐ������߂�
                // �ڐ��̒P�ʖ@���x�N�g���𓾂�B(1-j*2)�͕���
                Point n = v.multiply(new Point(sin, cos * (1 - j * 2)));
                ret[i][j] = new Point[] {
                        a.add(n.multiply(ar)), b.add(n.multiply(sign * br)) };  // ���ڐ��͕Е��t���ɂȂ�
                // �ڐ����d�Ȃ�Ƃ���ret[i][j]�͐����ɂȂ�Ȃ��̂ňȉ��̂悤�ɕ��������𑫂��Ă����ƕ֗���������Ȃ� 
                // if (cos < EPS) ret[i][j][1] = ret[i][j][1].add(n.multiply(new Point(0,1)));
                if (cos < EPS) break;  // �ڐ����d�Ȃ��Ă���i�d������ʂ��Ȃ��Ȃ炱�̍s���폜�j
            }
            // i++; // ���ڐ��ƊO�ڐ�����ʂ��Ȃ��Ȃ�i�̃C���N�������g�ʒu�������ɕς���
        }
        return ret;  // ���ڐ��ƊO�ڐ�����ʂ���ret.length�Őڐ��̑g�̐��𓾂�ɂ�
                    // return Arrays.copyOf(ret, i); �Ƃ���i�vJava6�j
    }

    static Point circumcenter(Point a, Point b, Point c) {
        // 2�{�̐����񓙕����̌�_�����߂�
        a = a.subtract(c).multiply(0.5);
        Point an = a.multiply(new Point(0, 1));
        b = b.subtract(c).multiply(0.5);
        Point bn = b.multiply(new Point(0, 1));
        return crosspointLL(a, a.add(an), b, b.add(bn)).add(c);
    }

    /**
     * ps�͑��p�`�łȂ��Ă悢�BO(n)�H
     * 
     * @return �ŏ���܉~�̒��S
     * @see http://www.ipsj.or.jp/07editj/promenade/4309.pdf
     */
    static Point minEnclosingCircle(Point[] ps) {
        int n = ps.length;
        Point c = new Point();
        double move = 0.5;
        for (int i = 0; i < 39; i++) {  // 2^(-39-1) \approx 0.9e-12
            for (int t = 0; t < 50; t++) {
                double max = 0;
                int k = 0;
                for (int j = 0; j < n; j++) {
                    if (max < ps[j].distanceSq(c)) {
                        max = ps[j].distanceSq(c);
                        k = j;
                    }
                }
                c = c.add(ps[k].subtract(c).multiply(move));
            }
            move /= 2;
        }
        return c;
    }

    /*
     * �ʑ��p�`
     */

    /**
     * Andrew's Monotone Chain�B�����̔z��̓\�[�g�����B
     * 
     * @return ccw�ȓʕ�
     * @see http://www.prefield.com/algorithm/geometry/convex_hull.html
     * @see http://en.wikibooks.org/wiki/Algorithm_Implementation/Geometry/Convex_hull/Monotone_chain
     */
    static ArrayList<Point> convexHull(Point[] ps) {
        Arrays.sort(ps);
        int n = ps.length;
        ArrayList<Point> hull = new ArrayList<Point>(n);
        // �����ʕ�
        hull.add(ps[0]);
        hull.add(ps[1]);
        int k = 2; // k == hull.size()
        for (int i = 2; i < n; i++, k++) {
            while (k >= 2) {
                if (ccw(hull.get(k - 2), hull.get(k - 1), ps[i]) == -1) {  // ���꒼����̓_���܂܂Ȃ��Ȃ� <= 0 ��
                    // �Ō�̃x�N�g�����猩�Č��_���E���ɂ���Ȃ�A�Ō�̓_���͂���
                    hull.remove(--k);
                }
                else break;
            }
            hull.add(ps[i]);
        }
        // �����ʕ�̌�ɏ㑤��������
        int lowerEnd = k++;
        hull.add(ps[n - 2]);
        for (int i = n - 3; i >= 0; i--, k++) {
            while (k > lowerEnd) {
                if (ccw(hull.get(k - 2), hull.get(k - 1), ps[i]) == -1) {  // ���꒼����̓_���܂܂Ȃ��Ȃ� <= 0 ��
                    hull.remove(--k);
                }
                else break;
            }
            hull.add(ps[i]);
        }
        hull.remove(--k);  // �Ō�͏d�����Ă���̂ŏ���
        return hull;
    }

    /**
     * O(n)�B
     * 
     * @return ���p�`�������̓ʁi�k�މj�Ȃ�true
     * @see http://www.prefield.com/algorithm/geometry/isconvex.html
     */
    static boolean isCcwConvex(Point[] polygon) {
        int n = polygon.length;
        for (int i = 0; i < n; i++) {
            Point cur = polygon[i];
            Point next = polygon[(i + 1) % n];
            Point next2 = polygon[(i + 2) % n];
            if (ccw(cur, next, next2) == -1) return false;
            // ���k�ނ�F�߂Ȃ��Ȃ� != 1 �Ƃ���
        }
        return true;
    }

    /**
     * O(n)�B�ʌ`�̌����͂ǂ���ł�����
     */
    static boolean isConvex(Point[] polygon) {
        int n = polygon.length;
        boolean isClockwise = true;
        boolean isCounterClockwise = true;
        for (int i = 0; i < n; i++) {
            Point cur = polygon[i];
            Point next = polygon[(i + 1) % n];
            Point next2 = polygon[(i + 2) % n];
            int ccw = ccw(cur, next, next2);
            if (ccw == 1) {
                if (!isCounterClockwise) return false;
                isClockwise = false;
            }
            else if (ccw == -1) {
                if (!isClockwise) return false;
                isCounterClockwise = false;
            }
        }
        return true;
    }

    /**
     * O(n)
     * 
     * @return �����ɑ��݂���Ȃ�1�A������Ȃ�2�A�O���Ȃ�0
     */
    static int isInConvex(Point p, Point[] polygon) {
        int n = polygon.length;
        int dir = ccw(polygon[0], polygon[1], p);
        for (int i = 0; i < n; i++) {
            Point cur = polygon[i];
            Point next = polygon[(i + 1) % n];
            int ccw = ccw(cur, next, p);
            if (ccw == 0) return 2;  // ������ɑ���
            if (ccw != dir) return 0;
        }
        return 1;
    }

    /**
     * O(log n)
     * ���p�`�͏k�ނ��Ă��Ȃ�����
     * 
     * @return �����ɑ��݂���Ȃ�1�A������Ȃ�2�A�O���Ȃ�0
     * @see http://www.prefield.com/algorithm/geometry/convex_contains.html
     * @see http://stackoverflow.com/a/5224119/897061
     */
    static int isInCcwConvex(Point p, Point[] polygon) {
        int n = polygon.length;
        // �ʌ`�̓��_��C�ӂɑI��
        Point g = polygon[0].add(polygon[n / 3]).add(polygon[n * 2 / 3])
                .multiply(1.0 / 3);
        if (g.equals(p)) return 1;
        Point gp = p.subtract(g);

        int l = 0;
        int r = n;
        while (l + 1 < r) {  // �_g�ɂ��̈���Z�N�^�ɕ������A�񕪒T���œ_p�̂���Z�N�^��T��
            int mid = (l + r) / 2;
            Point gl = polygon[l].subtract(g);
            Point gm = polygon[mid].subtract(g);
            if (gl.cross(gm) > 0) { // gl����gm�ւ̃Z�N�^���s�p�ł���
                // gl����gm�͈̔͂ɓ_p�����邩���ׂ�
                if (gl.cross(gp) >= 0 && gm.cross(gp) <= 0) r = mid;
                else l = mid;
            }
            else {
                // �s�p���ɓ_�����邩���ׂ�
                if (gm.cross(gp) >= 0 && gl.cross(gp) <= 0) l = mid;
                else r = mid;
            }
        }
        r %= n;
        double cross = polygon[l].subtract(p).cross(polygon[r].subtract(p));
        return approxEquals(cross, 0) ? 2 : cross < 0 ? 0 : 1;
    }

    /**
     * O(n)�B
     * Java�Ȃ�Polygon��Path2D�Ő}�������Area#contains()�g�����ق����y�B�ǂꂭ�炢�덷�����邩�m��Ȃ����ǁB
     * 
     * @return �����ɑ��݂���Ȃ�1�A������Ȃ�2�A�O���Ȃ�0
     * @see http://www.prefield.com/algorithm/geometry/contains.html
     */
    static int isInPolygon(Point p, Point[] polygon) {
        int n = polygon.length;
        boolean in = false;
        for (int i = 0; i < n; i++) {
            Point a = polygon[i].subtract(p);
            Point b = polygon[(i + 1) % n].subtract(p);
            if (approxEquals(a.cross(b), 0) && a.dot(b) < EPS) return 2;
            if (a.y > b.y) {  // �_�Ƃ̈ʒu�֌W�����߂邽�߃x�N�g��ab��������ɂ���
                Point temp = a;
                a = b;
                b = temp;
            }
            // �����������Ɗ��S�Ɍ������Ă��邩�A�����̒[�_��
            // �����̏㑤�ɂ���Ȃ璼���ƌ�������Ƃ݂Ȃ�
            if (a.y * b.y < 0 || (a.y * b.y < EPS && b.y > EPS)) {
                if (a.cross(b) < EPS) in = !in;  // �O�ς����Ȃ甼�����Ƃ���������
            }
        }
        return in ? 1 : 0;
    }

    /**
     * O(nlogm + mlogn)�B�����璷�Ȏ����B
     * Java�Ȃ�Polygon��Path2D�Ő}�������Area#intersect(Area)���g�����ق����y�ȏ��
     * �ʂ���Ȃ��}�`�ɂ��g���邪�A���\�h��ɐۓ�����̂Ŗʐϋ��߂�Ƃ��ȊO�͌덷���������B
     * 
     * @return p��q�̋��ʕ���
     * @see http://www.prefield.com/algorithm/geometry/convex_intersect.html
     * @see http://web.archive.org/web/20110317231822/http://cgm.cs.mcgill.ca/~godfried/teaching/cg-projects/97/Plante/
     *      CompGeomProject-EPlante/algorithm.html
     */
    static ArrayList<Point> convexIntersection(Point[] p, Point[] q) {
        int n = p.length;
        int m = q.length;
        Point minP = Collections.min(Arrays.asList(p));
        Point minQ = Collections.min(Arrays.asList(q));
        int argminP = Arrays.asList(p).indexOf(minP);
        int argminQ = Arrays.asList(q).indexOf(minQ);
        int comp = minP.compareTo(minQ);
        int i = argminP;
        int j = argminQ;
        if (comp == 0) {
            // �ŏ��̗v�f����v����Ȃ�A2�̓ʑ��p�`�𓯎��ɐi��ł���
            // �ŏ��Ɍ�����������_�����������ł���
            do {
                i = (i + 1) % n;
                j = (j + 1) % m;
            } while (i != argminP && p[i].equals(q[j]));
            if (i == argminP) return new ArrayList<Point>(Arrays.asList(p));  // p��q�͓�����
            return convexIntersectionPhase3(p, q, (i + n - 1) % n, (j + m - 1) % m);
        }
        if (comp > 0) {  // p��q���E(���邢�͏�)�ɂ���
            return convexIntersection(q, p);  // �ӑĂ�swap
        }

        // p��q��荶�ɂ���Ȃ�L�����p�[����
        int count = 0;
        do {
            Point pVec = p[next(i, n)].subtract(p[i]);
            Point qVec = q[next(j, m)].subtract(q[j]);
            double cross = pVec.cross(qVec);
            Point dir = cross > 0 ? pVec : qVec;
            if (dir.cross(q[j].subtract(p[i])) < EPS) {
                // p�̃L�����p�[���猩��q�̃L�����p�[�͉E�����꒼����ɂ���B������pokect lid
                return convexIntersectionPhase2(p, q, i, j);
            }

            if (cross > -EPS) i = next(i, n);
            if (cross < EPS) j = next(j, m);
        } while (count++ < n + m);

        // ����ȏサ�Ă��L�����p�[�̈ʒu�֌W���ς��Ȃ������̂�q��p�̓����ɂ���
        return new ArrayList<Point>(Arrays.asList(q));
    }

    private static int next(int i, int n) {
        return (i + 1) % n;
    }

    private static ArrayList<Point> convexIntersectionPhase2(Point[] p, Point[] q, int i,
            int j) {
        int n = p.length;
        int m = q.length;
        // System.out.println("convexIntersectionREVENGEPhase2()" + i + ", " + j);
        // pocket lid�̒��Ɍ�������������͂��Ȃ̂ŒT��
        // ���͎��̒��ɂ���
        // �O��������p[0]�͋��ʕ����O�Ȃ̂ŁAp�̓C���f�b�N�X��i�߂邲�ƂɎ��̉��ɍs���A
        // q�̓C���f�b�N�X��߂����Ƃɉ��֍s��
        boolean updated;
        int count = 0;
        do {
            updated = false;
            while (count < n + m
                    && p[next(i, n)].subtract(p[i]).cross(
                            q[(j + m - 1) % m].subtract(p[i])) < -EPS) {
                j = (j + m - 1) % m;
                updated = true;
                count++;
            }
            while (count < n + m
                    && q[(j + m - 1) % m].subtract(q[j]).cross(
                            p[next(i, n)].subtract(q[j])) > EPS) {
                i = next(i, n);
                updated = true;
                count++;
            }
        } while (updated);
        if (count >= n + m) {  // ���ʕ����ȂǂȂ�����
            return new ArrayList<Point>();
        }
        j = (j + m - 1) % m;  // q�����v���ɍl���Ă����̂Ŗ߂�
        return convexIntersectionPhase3(p, q, i, j);
    }

    private static ArrayList<Point> convexIntersectionPhase3(Point[] p, Point[] q, int i,
            int j) {
        // ��̓I�ȋ��ʕ��������������̂ŁA���Ƃ͌��݌ʂ��g���[�X���Ă����΂����c�͂�
        int n = p.length;
        int m = q.length;
        assert intersectsSS(p[i], p[next(i, n)], q[j], q[next(j, m)]);
        // System.out.println("convexIntersectionREVENGEPhase3()" + i + ", " + j);
        // System.out.println(intersectsSS(p[i], p[next(i, n)], q[j], q[next(j, m)]));
        // System.out.println(isInCcwConvex(p[next(i, n)], q));

        ArrayList<Point> intersection = new ArrayList<Point>();
        Point crossP = crosspointLL(p[i], p[next(i, n)], q[j], q[next(j, m)]);
        intersection.add(crossP);
        boolean pIsInQ = p[next(i, n)].subtract(p[i]).cross(q[next(j, m)].subtract(q[j])) <= 0;
        if (pIsInQ && !p[next(i, n)].equals(q[j])) j = next(j, m);
        else i = next(i, n);
        // System.out.println("pisinq" + pIsInQ + ", " + Arrays.toString(p));
        // System.out.println(i + ", " + j);

        // �����܂�O(n+m)����������������O(nlogm + mlogn)�ɂȂ�܂��i�݌v�~�X�j
        // �L�����p�[�Ŏ��O�Ɍ����_�񋓂����O(n+m)���\�B
        // Spaghetti Source�̃R�[�h�͎��O�񋓂��ĂȂ�����O(n+m)�B�����Ăĉ�����Ă�̂���B
        do {
            Point nextP = p[next(i, n)];
            Point nextQ = q[next(j, m)];
            if (pIsInQ) {
                int inState = isInCcwConvex(nextP, q);
                if (inState == 1
                        || (inState == 2 && nextP.subtract(p[i]).cross(
                                nextQ.subtract(q[j])) < EPS)) {
                    intersection.add(nextP);
                }
                else {
                    // ��������ӂ�������܂�q�̕ӂ�i�߂�
                    while (!intersectsSS(p[i], nextP, q[j], q[next(j, m)])
                            || p[i].equals(q[j]))
                        j = (j + 1) % m;
                    nextQ = q[next(j, m)];
                    Point c = crosspointLL(p[i], nextP, q[j], nextQ);
                    if (approxEquals(nextP.subtract(p[i]).cross(nextQ.subtract(q[j])), 0)) {  // 2�x�N�g�������꒼����
                        // q�ɂƂ��čł��i�񂾏ꏊ�֍s���ׂ�
                        if (intersectsSP(p[i], nextP, nextQ)) c = nextQ;
                        else c = nextQ.subtract(nextP).distanceSqr() > nextQ.subtract(
                                p[i]).distanceSqr() ? p[i] : nextP;
                    }
                    intersection.add(c);
                    pIsInQ = false;
                }
                i = (i + 1) % n;
            }
            else {
                int inState = isInCcwConvex(nextQ, p);
                if (inState == 1
                        || (inState == 2 && nextQ.subtract(q[j]).cross(
                                nextP.subtract(p[i])) < EPS)) {
                    intersection.add(nextQ);
                }
                else {
                    while (!intersectsSS(p[i], p[next(i, n)], q[j], nextQ)
                            || p[i].equals(q[j]))
                        i = (i + 1) % n;
                    nextP = p[next(i, n)];
                    Point c = crosspointLL(p[i], nextP, q[j], nextQ);
                    if (approxEquals(nextP.subtract(p[i]).cross(nextQ.subtract(q[j])), 0)) {  // 2�x�N�g�������꒼����
                        // p�ɂƂ��čł��i�񂾏ꏊ�֍s���ׂ�
                        if (intersectsSP(q[j], nextQ, nextP)) c = nextP;
                        else c = nextP.subtract(nextQ).distanceSqr() > nextP.subtract(
                                q[j]).distanceSqr() ? q[j] : nextQ;
                    }
                    intersection.add(c);
                    pIsInQ = true;
                }
                j = (j + 1) % m;
            }
        } while (intersection.size() <= (n + m) * 2
                && !intersection.get(0).equals(intersection.get(intersection.size() - 1)));
        if (intersection.size() > (n + m) * 2) throw new IllegalStateException("A BUG");

        // ���ʂȕӂ�����
        ArrayList<Point> intersection2 = new ArrayList<Point>();
        for (int k = 0; k < intersection.size(); k++) {
            if (intersection2.size() < 2
                    || ccw(intersection2.get(intersection2.size() - 2), intersection2
                            .get(intersection2.size() - 1), intersection.get(k)) == 1) {
                intersection2.add(intersection.get(k));
            }
        }
        if (intersection2.size() > 1
                && intersection2.get(0).equals(
                        intersection2.get(intersection2.size() - 1))) intersection2
                .remove(intersection2.size() - 1);
        return intersection2;
    }

    /**
     * �ʑ��p�`�̒����̉E���ɂ��镔����؂�̂Ă�
     * 
     * @param ps
     * @param a1
     * @param a2
     * @return
     * @see http://www.prefield.com/algorithm/geometry/convex_cut.html
     */
    static ArrayList<Point> convexCut(Point[] ps, Point a1, Point a2) {
        int n = ps.length;
        ArrayList<Point> ret = new ArrayList<Point>(n + 1);
        for (int i = 0; i < n; i++) {
            int ccw = ccw(a1, a2, ps[i]);
            if (ccw != -1) ret.add(ps[i]);
            int ccwn = ccw(a1, a2, ps[(i + 1) % n]);
            if (ccw * ccwn == -1) {  // ???????
                ret.add(crosspointLL(a1, a2, ps[i], ps[(i + 1) % n]));
            }
        }
        return ret;
    }

    /**
     * O(n)
     * 
     * @return �ʑ��p�`�̒��a�ƂȂ�2�_�̑g
     * @see http://www.prefield.com/algorithm/geometry/convex_diameter.html
     */
    static int[] convexDiameter(Point[] ps) {
        int n = ps.length;
        int initI = 0, initJ = 0;
        for (int i = 1; i < n; i++) {
            if (ps[i].x < ps[initI].x) initI = i;
            if (ps[i].x > ps[initJ].x) initJ = i;
        }
        int i = initI, j = initJ;
        int maxI = i, maxJ = j;
        double maxDSqr = 0;
        int count = 0;
        do {
            if (maxDSqr < ps[i].distanceSq(ps[j])) {
                maxDSqr = ps[i].distanceSq(ps[j]);
                maxI = i;
                maxJ = j;
            }
            int ni = (i + 1) % n;
            int nj = (j + 1) % n;
            // i���̋t�����̃x�N�g�����猩��j���̃x�N�g�����E�����Ȃ�j��i�߂�
            if (ps[i].subtract(ps[ni]).cross(ps[nj].subtract(ps[j])) <= 0) j = nj;
            else i = ni;
        } while (count++ <= 2 * n);
        // Spaghetti Source�ɕ키�ƃ��[�v������(i != initI || j != initJ)�Ƃ��ׂ������A
        // �k�ނ������p�`�ɑ΂��������[�v������̂Ń��o�X�g�Ȏ����ɂ���
        return new int[] { maxI, maxJ };
    }

    /**
     * @see http://www.prefield.com/algorithm/geometry/area2.html
     */
    static double area(Point[] polygon) {
        double a = 0;
        for (int i = 0; i < polygon.length; i++) {
            a += polygon[i].cross(polygon[(i + 1) % polygon.length]);
        }
        return a / 2;
    }
    
    /**
     * @return ���p�`�̊􉽊w�I�d�S
     * @see http://izumi-math.jp/F_Nakamura/heso/heso3.htm
     */
    static Point centroid(Point[] ps) {
        int n = ps.length;
        double areaSum = 0;
        Point centroid = new Point();
        for (int i = 0; i < n; i++) {
            double area = ps[i].cross(ps[(i + 1) % n]);
            areaSum += area;
            Point center3 = ps[i].add(ps[(i + 1) % n]);
            centroid = centroid.add(center3.multiply(area));
        }
        return centroid.multiply(1 / (areaSum * 3));
    }


    /**
     * @param segs �����̃��X�g
     * @param ps �O���t�ƍ��W�̑Ή�������o�͕ϐ�
     * @return �����A�����W�����g�̃O���t�B���ړI�ȕӂ͏ȗ����Ă���B
     * @see http://www.prefield.com/algorithm/geometry/segment_arrangement.html
     */
    static ListsGraph segmentArrangement(Point[][] segs, List<Point> ps) {  // ListsGraph��Graphs.java�ɂ���
        int n = segs.length;
        TreeSet<Point> set = new TreeSet<Point>();  // Java�̃��X�g�ɂ�C++��unique���Ȃ��̂Łc
        for (int i = 0; i < n; i++) {
            set.add(segs[i][0]);
            set.add(segs[i][1]);
            for (int j = i + 1; j < n; j++) {
                if (intersectsSS(segs[i][0], segs[i][1], segs[j][0], segs[j][1])) {
                    // assert !intersectsSS(segs[i][0], segs[j][0], segs[i][1], segs[i][1]);
                    set.add(crosspointLL(segs[i][0], segs[i][1], segs[j][0], segs[j][1]));
                }
            }
        }
        ps.addAll(set);
        ListsGraph g = new ListsGraph(ps.size());

        class CP implements Comparable<CP> {  // JAVA��pair�͂Ȃ�!!!
            final int i;
            final double d;

            CP(int i, double d) {
                this.i = i;
                this.d = d;
            }

            @Override
            public int compareTo(CP o) {
                return (int) Math.signum(d - o.d);
            }
        }
        ArrayList<CP> list = new ArrayList<CP>(ps.size());
        for (int i = 0; i < n; i++) {
            list.clear();
            for (int j = 0; j < ps.size(); j++) {
                if (intersectsSP(segs[i][0], segs[i][1], ps.get(j))) list.add(new CP(j,
                        segs[i][0].distanceSq(ps.get(j))));
            }
            Collections.sort(list);
            for (int j = 0; j + 1 < list.size(); j++) {
                int a = list.get(j).i;
                int b = list.get(j + 1).i;
                g.addEdge(new Edge(a, b, (int) ps.get(a).distance(ps.get(b))));
                // ��������Edge�N���X�̎d�l�ɍ��킹��int�ɃL���X�g���Ă��邪���ۂ̓L���X�g���Ȃ�
            }
        }
        return g;
    }
}
