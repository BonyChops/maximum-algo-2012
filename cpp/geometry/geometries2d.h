#include <iostream>
#include <vector>
#include <algorithm>
#include <map>
#include <math.h>
#include <complex>
using namespace std;
#define rep(i,n) for (int i=0; i < (n); i++)

// ��Ɉȉ��̎������Q�l�ɍ쐬�����B
// - http://www.prefield.com/algorithm
// - http://www.deqnotes.net/acmicpc/2d_geometry/
// - https://github.com/infnty/acm/tree/master/lib/geometry
// - �T�[�N���̐�y����������C�u����

/* ��{�v�f */

typedef double D;      // ���W�l�̌^�Bdouble��long double��z��
typedef complex<D> P;  // Point
typedef pair<P, P> L;  // Line
typedef vector<P> VP;
const D EPS = 1e-9;    // ���e�덷�B���ɂ���ĕς���
#define X real()
#define Y imag()
#define LE(n,m) ((n) < (m) + EPS)
#define GE(n,m) ((n) + EPS > (m))
#define EQ(n,m) (abs((n)-(m)) < EPS)

// ���ρ@dot(a,b) = |a||b|cos��
D dot(P a, P b) {
  return (conj(a)*b).X;
}
// �O�ρ@cross(a,b) = |a||b|sin��
D cross(P a, P b) {
  return (conj(a)*b).Y;
}

// �_�̐i�s����
int ccw(P a, P b, P c) {
  b -= a;  c -= a;
  if (cross(b,c) >  EPS) return +1;  // counter clockwise
  if (cross(b,c) < -EPS) return -1;  // clockwise
  if (dot(b,c)   < -EPS) return +2;  // c--a--b on line
  if (norm(b) < norm(c)) return -2;  // a--b--c on line or a==b
  return 0;                          // a--c--b on line or a==c or b==c
}

/* ��������@�����E�����͏k�ނ��Ă͂Ȃ�Ȃ��B�ڂ���ꍇ�͌�������Ƃ݂Ȃ��Bisec��intersect�̗� */

// �����Ɠ_
bool isecLP(P a1, P a2, P b) {
  return abs(ccw(a1, a2, b)) != 1;  // return EQ(cross(a2-a1, b-a1), 0); �Ɠ���
}

// �����ƒ���
bool isecLL(P a1, P a2, P b1, P b2) {
  return !isecLP(a2-a1, b2-b1, 0) || isecLP(a1, b1, b2);
}

// �����Ɛ���
bool isecLS(P a1, P a2, P b1, P b2) {
  return cross(a2-a1, b1-a1) * cross(a2-a1, b2-a1) < EPS;
}

// �����Ɛ���
bool isecSS(P a1, P a2, P b1, P b2) {
  return ccw(a1, a2, b1)*ccw(a1, a2, b2) <= 0 &&
         ccw(b1, b2, a1)*ccw(b1, b2, a2) <= 0;
}

// �����Ɠ_
bool isecSP(P a1, P a2, P b) {
  return !ccw(a1, a2, b);
}


/* �����@�e�����E�����͏k�ނ��Ă͂Ȃ�Ȃ� */

// �_p�̒���a�ւ̎ˉe�_��Ԃ�
P proj(P a1, P a2, P p) {
  return a1 + dot(a2-a1, p-a1)/norm(a2-a1) * (a2-a1);
}

// �_p�̒���a�ւ̔��˓_��Ԃ�
P reflection(P a1, P a2, P p) {
  return 2.0*proj(a1, a2, p) - p;
}

D distLP(P a1, P a2, P p) {
  return abs(proj(a1, a2, p) - p);
}

D distLL(P a1, P a2, P b1, P b2) {
  return isecLL(a1, a2, b1, b2) ? 0 : distLP(a1, a2, b1);
}

D distLS(P a1, P a2, P b1, P b2) {
  return isecLS(a1, a2, b1, b2) ? 0 : min(distLP(a1, a2, b1), distLP(a1, a2, b2));
}

D distSP(P a1, P a2, P p) {
  P r = proj(a1, a2, p);
  if (isecSP(a1, a2, r)) return abs(r-p);
  return min(abs(a1-p), abs(a2-p));
}

D distSS(P a1, P a2, P b1, P b2) {
  if (isecSS(a1, a2, b1, b2)) return 0;
  return min(min(distSP(a1, a2, b1), distSP(a1, a2, b2)),
             min(distSP(b1, b2, a1), distSP(b1, b2, a2)));
}

// 2�����̌�_
P crosspointLL(P a1, P a2, P b1, P b2) {
  D d1 = cross(b2-b1, b1-a1);
  D d2 = cross(b2-b1, a2-a1);
  if (EQ(d1, 0) && EQ(d2, 0)) return a1;  // same line
  if (EQ(d2, 0)) throw "kouten ga nai";   // ��_���Ȃ�
  return a1 + d1/d2 * (a2-a1);
}


/* �~ */

D distLC(P a1, P a2, P c, D r) {
  return max(distLP(a1, a2, c) - r, 0.0);
}

D distSC(P a1, P a2, P c, D r) {
  D dSqr1 = norm(c-a1), dSqr2 = norm(c-a2);
  if (dSqr1 < r*r ^ dSqr2 < r*r) return 0;  // �~���������܂���Ƃ�����0�Ȃ炱����OR�ɕς���
  if (dSqr1 < r*r & dSqr2 < r*r) return r - sqrt(max(dSqr1, dSqr2));
  return max(distSP(a1, a2, c) - r, 0.0);
}

VP crosspointLC(P a1, P a2, P c, D r) {
  VP ps;
  P ft = proj(a1, a2, c);
  if (!GE(r*r, norm(ft-c))) return ps;

  P dir = sqrt(max(r*r - norm(ft-c), 0.0)) / abs(a2-a1) * (a2-a1);
  ps.push_back(ft + dir);
  if (!EQ(r*r, norm(ft-c))) ps.push_back(ft - dir);
  return ps;
}

D distCC(P a, D ar, P b, D br) {
  D d = abs(a-b);
  return GE(d, abs(ar-br)) ? max(d-ar-br, 0.0) : abs(ar-br) - d;
}

// 2�~�̌�_
VP crosspointCC(P a, D ar, P b, D br) {
  VP ps;
  P ab = b-a;
  D d = abs(ab);
  D crL = (norm(ab) + ar*ar - br*br) / (2*d);
  if (EQ(d, 0) || ar < crL) return ps;

  P abN = ab * P(0, sqrt(ar*ar - crL*crL) / d);
  P cp = a + crL/d * ab;
  ps.push_back(cp + abN);
  if (!EQ(norm(abN), 0)) ps.push_back(cp - abN);
  return ps;
}

// �_p����~a�ւ̐ڐ��̐ړ_
VP tangentPoints(P a, D ar, P p) {
  VP ps;
  D sin = ar / abs(p-a);
  if (!LE(sin, 1)) return ps;  // ������NaN���e�����
  D t = M_PI_2 - asin(min(sin, 1.0));
  ps.push_back(                 a + (p-a)*polar(sin, t));
  if (!EQ(sin, 1)) ps.push_back(a + (p-a)*polar(sin, -t));
  return ps;
}

// 2�~�̋��ʐڐ��B�Ԃ����e�����Ɋ܂܂�钸�_�͉~�Ƃ̐ړ_�ƂȂ�
vector<L> tangentLines(P a, D ar, P b, D br) {
  vector<L> ls;
  D d = abs(b-a);
  rep (i,2) {
    D sin = (ar - (1-i*2)*br) / d;
    if (!LE(sin*sin, 1)) break;
    D cos = sqrt(max(1 - sin*sin, 0.0));
    rep (j,2) {
      P n = (b-a) * P(sin, (1-j*2)*cos) / d;
      ls.push_back(L(a + ar*n, b + (1-i*2)*br*n));
      if (cos < EPS) break;  // �d������ڐ��𖳎��i�d�����Ă����Ȃ炱�̍s�s�v�j
    }
  }
  return ls;
}

// �O�p�`�̊O�S�B�_a,b,c�͓������ɂ����Ă͂Ȃ�Ȃ�
P circumcenter(P a, P b, P c) {
  a = (a-c)*0.5;
  b = (b-c)*0.5;
  return c + crosspointLL(a, a*P(1,1), b, b*P(1,1));
}

// �_a�Ɠ_b��ʂ�A���a��r�̉~�̒��S��Ԃ�
VP circlesPointsRadius(P a, P b, D r) {
  VP cs;
  P abH = (b-a)*0.5;
  D d = abs(abH);
  if (d == 0 || d > r) return cs;  // �K�v�Ȃ� !LE(d,r) �Ƃ��ĉ~1�ɂȂ鑤�֊ۂ߂�
  D dN = sqrt(r*r - d*d);          // �K�v�Ȃ� max(r*r - d*d, 0) �Ƃ���
  P n = abH * P(0,1) * (dN / d);
  cs.push_back(a + abH + n);
  if (dN > 0) cs.push_back(a + abH - n);
  return cs;
}

// �_a�Ɠ_b��ʂ�A����l�ɐڂ���~�̒��S
VP circlesPointsTangent(P a, P b, P l1, P l2) {
  P n = (l2-l1) * P(0,1);
  P m = (b-a) * P(0,0.5);
  D rC = dot((a+b)*0.5-l1, n);
  D qa = norm(n)*norm(m) - dot(n,m)*dot(n,m);
  D qb = -rC * dot(n,m);
  D qc = norm(n)*norm(m) - rC*rC;
  D qd = qb*qb - qa*qc;  // qa*k^2 + 2*qb*k + qc = 0

  VP cs;
  if (qd < -EPS) return cs;
  if (EQ(qa, 0)) {
    if (!EQ(qb, 0)) cs.push_back((a+b)*0.5 - m * (qc/qb/2));
    return cs;
  }
  D t = -qb/qa;
  cs.push_back(              (a+b)*0.5 + m * (t + sqrt(max(qd, 0.0))/qa));
  if (qd > EPS) cs.push_back((a+b)*0.5 + m * (t - sqrt(max(qd, 0.0))/qa));
  return cs;
}

// �_�W�����܂ލŏ��̉~�̒��S
P minEnclosingCircle(const VP& ps) {
  P c;
  double move = 0.5;
  rep(i,39) {  // 2^(-39-1) \approx 0.9e-12
    rep(t,50) {
      D max = 0;
      int k = 0;
      rep (j, ps.size()) if (max < norm(ps[j]-c)) {
        max = norm(ps[j]-c);
        k = j;
      }
      c += (ps[k]-c) * move;
    }
    move /= 2;
  }
  return c;
}


/* ���p�` */

// ���_�̏����isort��max_element�ɕK�v�j
namespace std {
  bool operator<(const P a, const P b) {
    return a.X != b.X ? a.X < b.X : a.Y < b.Y;
  }
}

// �ʕ�
VP convexHull(VP ps) {  // ���̓_�W�����\�[�g����Ă����Ȃ�VP&��
  int n = ps.size(), k = 0;
  sort(ps.begin(), ps.end());
  VP ch(2*n);
  for (int i = 0; i < n; ch[k++] = ps[i++]) // lower-hull
    while (k >= 2 && ccw(ch[k-2], ch[k-1], ps[i]) <= 0) --k;  // �]�v�ȓ_���܂ނȂ� == -1 �Ƃ���
  for (int i = n-2, t = k+1; i >= 0; ch[k++] = ps[i--]) // upper-hull
    while (k >= t && ccw(ch[k-2], ch[k-1], ps[i]) <= 0) --k;
  ch.resize(k-1);
  return ch;
}

// �ʔ���B�k�ނ�F�߂Ȃ��Ȃ�ccw�̔��蕔���� != 1 �Ƃ���
bool isCcwConvex(const VP& ps) {
  int n = ps.size();
  rep (i, n) if (ccw(ps[i], ps[(i+1) % n], ps[(i+2) % n]) == -1) return false;
  return true;
}

// �ʑ��p�`�̓�������@O(n)
// �_���̈�����Ȃ�1�A���E��Ȃ�2�A�O���Ȃ�0��Ԃ�
int inConvex(P p, const VP& ps) {
  int n = ps.size();
  int dir = ccw(ps[0], ps[1], p);
  rep (i, n) {
    int ccwc = ccw(ps[i], ps[(i + 1) % n], p);
    if (!ccwc) return 2;  // ������ɑ���
    if (ccwc != dir) return 0;
  }
  return 1;
}

// �ʑ��p�`�̓�������@O(logn)
// �_���̈�����Ȃ�1�A���E��Ȃ�2�A�O���Ȃ�0��Ԃ�
int inCcwConvex(const VP& ps, P p) {
  int n = ps.size();
  P g = (ps[0] + ps[n / 3] + ps[n*2 / 3]) / 3.0;
  if (g == p) return 1;
  P gp = p - g;

  int l = 0, r = n;
  while (l + 1 < r) {
    int mid = (l + r) / 2;
    P gl = ps[l] - g;
    P gm = ps[mid] - g;
    if (cross(gl, gm) > 0) {
      if (cross(gl, gp) >= 0 && cross(gm, gp) <= 0) r = mid;
      else l = mid;
    }
    else {
      if (cross(gl, gp) <= 0 && cross(gm, gp) >= 0) l = mid;
      else r = mid;
    }
  }
  r %= n;
  D cr = cross(ps[l] - p, ps[r] - p);
  return EQ(cr, 0) ? 2 : cr < 0 ? 0 : 1;
}

// ���p�`�̓�������
// �_���̈�����Ȃ�1�A���E��Ȃ�2�A�O���Ȃ�0��Ԃ�
int inPolygon(const VP& ps, P p) {
  int n = ps.size();
  bool in = false;
  rep (i, n) {
    P a = ps[i] - p;
    P b = ps[(i + 1) % n] - p;
    if (EQ(cross(a,b), 0) && LE(dot(a,b), 0)) return 2;
    if (a.Y > b.Y) swap(a,b);
    if ((a.Y*b.Y < 0 || (a.Y*b.Y < EPS && b.Y > EPS)) && LE(cross(a, b), 0)) in = !in;
  }
  return in;
}

// �ʑ��p�`�N���b�s���O
VP convexCut(const VP& ps, P a1, P a2) {
  int n = ps.size();
  VP ret;
  rep(i,n) {
    int ccwc = ccw(a1, a2, ps[i]);
    if (ccwc != -1) ret.push_back(ps[i]);
    int ccwn = ccw(a1, a2, ps[(i + 1) % n]);
    if (ccwc * ccwn == -1) ret.push_back(crosspointLL(a1, a2, ps[i], ps[(i + 1) % n]));
  }
  return ret;
}

// �ʑ��p�`�̒��a�i�ŉ��_�΁j
pair<int, int> convexDiameter(const VP& ps) {
  int n = ps.size();
  int i = min_element(ps.begin(), ps.end()) - ps.begin();
  int j = max_element(ps.begin(), ps.end()) - ps.begin();
  int maxI, maxJ;
  D maxD = 0;
  rep(_, 2*n) {
    if (maxD < norm(ps[i]-ps[j])) {
      maxD = norm(ps[i]-ps[j]);
      maxI = i;
      maxJ = j;
    }
    if (cross(ps[i]-ps[(i+1) % n], ps[(j+1) % n]-ps[j]) <= 0) j = (j+1) % n;
    else                                                      i = (i+1) % n;
  }
  return make_pair(maxI, maxJ);
}

// ���p�`�̕����t�ʐ�
D area(const VP& ps) {
  D a = 0;
  rep (i, ps.size()) a += cross(ps[i], ps[(i+1) % ps.size()]);
  return a / 2;
}

// ���p�`�̊􉽊w�I�d�S
P centroid(const VP& ps) {
  int n = ps.size();
  D aSum = 0;
  P c;
  rep (i, n) {
    D a = cross(ps[i], ps[(i+1) % n]);
    aSum += a;
    c += (ps[i] + ps[(i+1) % n]) * a;
  }
  return 1 / aSum / 3 * c;
}

// �{���m�C�̈�
VP voronoiCell(P p, const VP& ps, const VP& outer) {
  VP cl = outer;
  rep (i, ps.size()) {
    if (EQ(norm(ps[i]-p), 0)) continue;
    P h = (p+ps[i])*0.5;
    cl = convexCut(cl, h, h + (ps[i]-h)*P(0,1) );
  }
  return cl;
}

/* �􉽃O���t */

struct Edge {
  int from, to;
  D cost;
  Edge(int from, int to, D cost) : from(from), to(to), cost(cost) {}
};
struct Graph {
  int n;
  vector<vector<Edge> > edges;
  Graph(int n) : n(n), edges(n) {}
  void addEdge(Edge e) {
    edges[e.from].push_back(e);
    edges[e.to].push_back(Edge(e.to, e.from, e.cost));
  }
};

// �����A�����W�����g�i�����̈ʒu�֌W����O���t���쐬�j
Graph segmentArrangement(const vector<L>& segs, VP& ps) {
  int n = segs.size();
  rep (i, n) {
    ps.push_back(segs[i].first);
    ps.push_back(segs[i].second);
    rep (j, i) {
      if (isecSS(                 segs[i].first, segs[i].second, segs[j].first, segs[j].second))
        ps.push_back(crosspointLL(segs[i].first, segs[i].second, segs[j].first, segs[j].second));
    }
  }
  sort(ps.begin(), ps.end());
  ps.erase(unique(ps.begin(), ps.end()), ps.end());

  int m = ps.size();
  Graph gr(m);
  vector<pair<D, int> > list;
  rep (i, n) {
    list.clear();
    rep (j, m) {
      if (isecSP(segs[i].first, segs[i].second, ps[j]))
        list.push_back(make_pair(norm(segs[i].first-ps[j]), j));
    }
    sort(list.begin(), list.end());
    rep (j, list.size() - 1) {
      int a = list[j  ].second;
      int b = list[j+1].second;
      gr.addEdge(Edge(a, b, abs(ps[a]-ps[b])));
    }
  }
  return gr;
}

// ���O���t�i�_�W�����猩����ʒu�֕ӂ𒣂����O���t�j
Graph visibilityGraph(const VP& ps, const vector<VP>& objs) {
  int n = ps.size();
  Graph gr(n);
  rep (i,n) rep (j,i) {
    P a = ps[i], b = ps[j];
    if (!EQ(norm(a-b), 0)) rep (k, objs.size()) {
      const VP& obj = objs[k];
      int inStA = inConvex(a, obj);
      int inStB = inConvex(b, obj);
      if ((inStA ^ inStB) % 2 || inStA * inStB != 1 && inConvex((a+b)*0.5, obj) == 1) goto skip;
      rep (l, obj.size()) {
        P cur = obj[l];
        P next = obj[(l + 1) % obj.size()];
        if (isecSS(a, b, cur, next) && !isecSP(cur, next, a) && !isecSP(cur, next, b)) goto skip;
      }
    }
    gr.addEdge( Edge(i, j, abs(a-b)) );
    skip: {}
  }
  return gr;
}


/* ���̑� */

// �d����������𕹍�����
vector<L> mergeSegments(vector<L> segs) {
  int n = segs.size();
  rep (i,n) if (segs[i].second < segs[i].first) swap(segs[i].second, segs[i].first);

  rep (i,n) rep (j,i) {
    L &l1 = segs[i], &l2 = segs[j];
    if (EQ(cross(l1.second-l1.first, l2.second-l2.first), 0)
        && isecLP(l1.first, l1.second, l2.first)
        && ccw   (l1.first, l1.second, l2.second) != 2
        && ccw   (l2.first, l2.second, l1.second) != 2) {
      segs[j] = L(min(l1.first, l2.first), max(l1.second, l2.second));
      segs[i--] = segs[--n];
      break;
    }
  }
  segs.resize(n);
  return segs;
}


// ���̕ӂɃR�[�h���ڂ���قǂł��Ȃ����d�v�Ȓ藝�Ƃ��}�Ƃ������Ă����Ƃ悢�C�����܂�

// �]���藝
// ��ABC �ɂ����āAa = BC, b = CA, c = AB �Ƃ����Ƃ�
// a^2 = b^2 + c^2 ? 2bc cos ��CAB

// �w�����̌���
// 3�ӂ̒�����a,b,c�ł���O�p�`�̖ʐ�T
// T = sqrt{ s(s-a)(s-b)(s-c) }, s = (a+b+c)/2

// �s�b�N�̒藝
// ���p�`�̒��_���S�Ċi�q�_��ɂ���A�����Ɍ����Ȃ��Ƃ�
// S = i + b/2 - 1 (S:���p�`�̖ʐ�, i: ���p�`�̓����ɂ���i�q�_�̐�, b: �ӏ�̊i�q�_�̐�)
