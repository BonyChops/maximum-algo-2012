#include <iostream>
#include <vector>
#include <algorithm>
#include <string.h>
using namespace std;

const int MAX_N = 1000; // n�̍ő�l
const int MAX_W = 5000; // W�̍ő�l

// ����
int n, W;
int w[MAX_N], v[MAX_N];

// i�Ԗڈȍ~�̕i������d���̘a��j�ȉ��Ȃ�悤�ɑI�񂾂Ƃ��́A
// ��肤�鉿�l�̑��a�̍ő�l��Ԃ��֐�
int rec(int i, int j) {
  int res;
  if (i == n) {
    // �i���������c���Ă��Ȃ��Ƃ��́A���l�̘a�̍ő�l��0�Ŋm��
    res = 0;
  } else if (j < w[i]) {
    // �c��̗e�ʂ����肸�i��i�������Ȃ��̂ŁA����Ȃ��p�^�[����������
    // i+1 �ȍ~�̕i���Ŕ��肵���Ƃ��̍ő�l�����̂܂܂��̏ꍇ�̍ő�l�ɂ���
    res = rec(i + 1, j);
  } else {
    // �i��i�����邩����Ȃ����I�ׂ�̂ŁA���������ĉ��l�̘a���傫������I��
    res = max(
        rec(i + 1, j),
        rec(i + 1, j - w[i]) + v[i]
    );
  }
  return res;
}

// �P���ȍċA��p������@
void solve() {
  // 0�Ԗڈȍ~�ŗe��W�ȉ��̏ꍇ�̌��ʂ�\������
  cout << rec(0, W) << endl;
}

// �������e�[�u��
// dp[i][j]��i�Ԗڈȍ~�̕i������d���̘a��j�ȉ��Ȃ�悤�ɑI�񂾂Ƃ��̉��l�̘a�̍ő�l��\���B
// -1�Ȃ�l��������ł��邱�Ƃ�\��
int dp[MAX_N + 1][MAX_W + 1]; 

// i�Ԗڈȍ~�̕i������d���̘a��j�ȉ��Ȃ�悤�ɑI�񂾂Ƃ��́A
// ��肤�鉿�l�̑��a�̍ő�l��Ԃ��֐��B�����z��Ōv�Z���ʂ��ė��p����
int rec_dp(int i, int j) {
  if (dp[i][j] != -1) {
    // ���łɒ��ׂ����Ƃ�����Ȃ炻�̌��ʂ��ė��p
    return dp[i][j];
  }
  int res;
  if (i == n) {
    // �i���������c���Ă��Ȃ��Ƃ��́A���l�̘a�̍ő�l��0�Ŋm��
    res = 0;
  } else if (j < w[i]) {
    // �c��̗e�ʂ����肸�i��i�������Ȃ��̂ŁA����Ȃ��p�^�[����������
    res = rec_dp(i + 1, j);
  } else {
    // �i��i�����邩����Ȃ����I�ׂ�̂ŁA���������ĉ��l�̘a���傫������I��
    res = max(
        rec_dp(i + 1, j),
        rec_dp(i + 1, j - w[i]) + v[i]
    );
  }
  // ���ʂ��e�[�u���ɋL������
  return dp[i][j] = res;
}

// �������ċA��p������@
void solve_dp() {
  memset(dp, -1, sizeof(dp)); // �������e�[�u����-1�ŏ������@�ȉ���for���[�v�Ɠ���
//  for (int i = 0; i < MAX_N + 1; i++)
//    for (int j = 0; j < MAX_W + 1; j++)
//      dp[i][j] = -1;
  
  // 0�Ԗڈȍ~�ŗe��W�ȉ��̏ꍇ�̌��ʂ�\������
  cout << rec_dp(0, W) << endl;
}

// DP�e�[�u���B
// dp2[i][j]��i�Ԗڈȍ~�̕i������d���̘a��j�ȉ��Ȃ�悤�ɑI�񂾂Ƃ��̉��l�̘a�̍ő�l��\���B
int dp2[MAX_N + 1][MAX_W + 1];

// �Q������p������@
void solve_dp2() {
  for (int j = 0; j <= W; j++) dp2[n][j] = 0;
  
  for (int i = n - 1; i >= 0; i--) {
    for (int j = 0; j <= W; j++) {
      if (j < w[i])
        dp2[i][j] = dp2[i + 1][j];
      else
        dp2[i][j] = max(dp2[i + 1][j], dp2[i + 1][j - w[i]] + v[i]);
    }
  }
  cout << dp2[0][W] << endl;
}


int main() {
  cin >> n >> W;
  for (int i = 0; i < n; i++) {
    cin >> w[i] >> v[i];
  }

  solve();  // �ċA
  solve_dp(); // �������ċA�ɂ��DP
  solve_dp2(); // �Q�������[�v�ɂ��DP

  return 0;
}

/*
 ���͂̌`���F
    n W
    w_1 v_1
    w_2 v_2
    ...
    w_n v_n

 ���͗�F
    6 15
    11 15
    2 3
    3 1
    5 8
    1 2
    4 4
 
 �o�͗�F
    20
	20
	20
*/
