#include <bits/stdc++.h>
#include "treap.h"

using namespace std;

// usage example
int main() {
    pNode t = nullptr;
    int pos = 0;
    for (int a: {1, 2, 7, 4, 5})
        insert(t, pos++, a);
    int n = t->size;
    for (int i = 0; i < n; ++i)
        cout << query(t, i, i).node_value << endl;
    modify(t, 1, 3, 10);
    for (int i = 0; i < n; ++i)
        cout << query(t, i, i).node_value << endl;
    for (int i = 0; i < n; ++i)
        remove(t, 0);
    cout << Node::get_size(t) << endl;

    clear(t);
}
