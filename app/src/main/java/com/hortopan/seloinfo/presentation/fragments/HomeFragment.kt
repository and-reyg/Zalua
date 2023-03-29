package com.hortopan.seloinfo.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import com.hortopan.seloinfo.R
import com.hortopan.seloinfo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем ссылки на DrawerLayout и NavigationView из макета
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView

        // Добавляем слушатель кликов на кнопку открытия меню
        binding.buttonOpenMenu.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }
/*
        // Добавляем слушатель кликов на элементы меню
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_ads -> {
                    findNavController().navigate(R.id.action_homeFragment_to_adsFragment)
                    true
                }
                R.id.menu_item_schedule -> {
                    findNavController().navigate(R.id.action_homeFragment_to_scheduleFragment)
                    true
                }
                R.id.menu_item_settings -> {
                    findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
                    true
                }
                else -> false
            }
        }

*/
    }
}