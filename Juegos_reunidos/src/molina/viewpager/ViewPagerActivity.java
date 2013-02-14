package molina.viewpager;

import com.viewpagerindicator.TabPageIndicator;

import molina.resources.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class ViewPagerActivity extends FragmentActivity {
	private static final String[] TITULOS = { "SUDOKU", "TRES EN RAYA",
			"BUSCAMINAS" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_pager);
		FragmentPagerAdapter adapter = new FragmentAdapterHelper(
				getSupportFragmentManager());
		ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setAdapter(adapter);

		// este es el PageIndicator de la librería friki que me enseñó Rica
		// bebiendo ginebra
		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}

	private class FragmentAdapterHelper extends FragmentPagerAdapter {

		public FragmentAdapterHelper(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int item) {
			// 0: Sudoku, 1: Tres en Raya, 2: Buscaminas
			Fragment f = null;
			item = item % TITULOS.length;
			switch (item) {
			case 0:
				f = new SudokuMainFragment();
				break;
			case 1:
				f = new TresEnRayaMainFragment();
				break;
			case 2:
				f = new BuscaminasMainFragment();
				break;
			}
			return f;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITULOS[position % TITULOS.length];
		}

		@Override
		public int getCount() {
			return TITULOS.length;
		}

	}
}
