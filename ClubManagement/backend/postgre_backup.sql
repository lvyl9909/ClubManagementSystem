PGDMP      $                |            swen90007_teamy    13.14    16.3     �
           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            �
           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            �
           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            �
           1262    16389    swen90007_teamy    DATABASE     z   CREATE DATABASE swen90007_teamy WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.UTF8';
    DROP DATABASE swen90007_teamy;
                swen90007_teamy_owner    false            �
           0    0    swen90007_teamy    DATABASE PROPERTIES     8   ALTER DATABASE swen90007_teamy SET "TimeZone" TO 'utc';
                     swen90007_teamy_owner    false                        2615    2200    public    SCHEMA     2   -- *not* creating schema, since initdb creates it
 2   -- *not* dropping schema, since initdb creates it
                swen90007_teamy_owner    false            �
           0    0    SCHEMA public    ACL     Q   REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;
                   swen90007_teamy_owner    false    5            �            1259    16395    test    TABLE     9   CREATE TABLE public.test (
    name "char"[] NOT NULL
);
    DROP TABLE public.test;
       public         heap    swen90007_teamy_owner    false    5            �
          0    16395    test 
   TABLE DATA           $   COPY public.test (name) FROM stdin;
    public          swen90007_teamy_owner    false    200   �       m
           2606    16402    test test_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.test
    ADD CONSTRAINT test_pkey PRIMARY KEY (name);
 8   ALTER TABLE ONLY public.test DROP CONSTRAINT test_pkey;
       public            swen90007_teamy_owner    false    200            �           826    16391     DEFAULT PRIVILEGES FOR SEQUENCES    DEFAULT ACL     \   ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON SEQUENCES TO swen90007_teamy_owner;
                   postgres    false            �           826    16393    DEFAULT PRIVILEGES FOR TYPES    DEFAULT ACL     X   ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON TYPES TO swen90007_teamy_owner;
                   postgres    false            �           826    16392     DEFAULT PRIVILEGES FOR FUNCTIONS    DEFAULT ACL     \   ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON FUNCTIONS TO swen90007_teamy_owner;
                   postgres    false            �           826    16390    DEFAULT PRIVILEGES FOR TABLES    DEFAULT ACL     Y   ALTER DEFAULT PRIVILEGES FOR ROLE postgres GRANT ALL ON TABLES TO swen90007_teamy_owner;
                   postgres    false            �
      x������ � �     